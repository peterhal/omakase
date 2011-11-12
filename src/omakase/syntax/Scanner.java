// Copyright 2011 Peter Hallam
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package omakase.syntax;

import com.google.common.collect.ImmutableList;
import omakase.syntax.tokens.*;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;
import omakase.util.SourceLocation;
import omakase.util.SourceRange;

/**
 * Converts text to tokens.
 */
public class Scanner {
  private final ErrorReporter reporter;
  private final SourceFile file;
  private int index;
  private final StringBuilder buffer = new StringBuilder();

  public Scanner(ErrorReporter reporter, SourceFile file) {
    this.reporter = reporter;
    this.file = file;
    this.index = 0;
  }

  public ImmutableList<Token> scanAll() {
    ImmutableList.Builder<Token> tokens = ImmutableList.builder();
    while (!atEnd()) {
      tokens.add(scanToken());
    }
    return tokens.build();
  }

  private Token scanToken() {
    skipWhitespaceAndComments();
    int startIndex = index;
    char ch = nextChar();
    switch (ch) {
    case '\0': return createToken(TokenKind.END_OF_FILE, startIndex);
    case '{': return createToken(TokenKind.OPEN_CURLY, startIndex);
    case '}': return createToken(TokenKind.CLOSE_CURLY, startIndex);
    case '(': return createToken(TokenKind.OPEN_PAREN, startIndex);
    case ')': return createToken(TokenKind.CLOSE_PAREN, startIndex);
    case '.': return createToken(TokenKind.PERIOD, startIndex);
    case ';': return createToken(TokenKind.SEMI_COLON, startIndex);
    case ',': return createToken(TokenKind.COMMA, startIndex);
    case '<':
      if (eatOpt('=')) {
        return createToken(TokenKind.LESS_EQUAL, startIndex);
      }
      return createToken(TokenKind.OPEN_ANGLE, startIndex);
    case '>':
      if (eatOpt('=')) {
        return createToken(TokenKind.GREATER_EQUAL, startIndex);
      }
      return createToken(TokenKind.CLOSE_ANGLE, startIndex);
    case '=':
      if (eatOpt('=')) {
        return createToken(TokenKind.EQUAL_EQUAL, startIndex);
      }
      return createToken(TokenKind.EQUAL, startIndex);
    case '!':
      if (eatOpt('=')) {
        return createToken(TokenKind.NOT_EQUAL, startIndex);
      }
      return createToken(TokenKind.BANG, startIndex);
    case '+': return createToken(TokenKind.PLUS, startIndex);
    case '-': return createToken(TokenKind.MINUS, startIndex);
    case '*': return createToken(TokenKind.STAR, startIndex);
    case '&':
      if (eatOpt('&')) {
        return createToken(TokenKind.AMPERSAND_AMPERSAND, startIndex);
      }
      return createToken(TokenKind.AMPERSAND, startIndex);
    case '|':
      if (eatOpt('|')) {
        return createToken(TokenKind.BAR_BAR, startIndex);
      }
      return createToken(TokenKind.BAR, startIndex);
    case '?': return createToken(TokenKind.QUESTION, startIndex);
    case ':': return createToken(TokenKind.COLON, startIndex);
    case '/': return createToken(TokenKind.SLASH, startIndex);
    case '\"': return scanStringLiteral(index);
    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
      return scanNumber(startIndex, ch);
    default:
      if (isIdentifierStartChar(ch)) {
        return scanIdentifierOrKeyword(startIndex, ch);
      }
      reportError(startIndex, "Unexpected character '%s'.", ch);
      return createToken(TokenKind.ERROR, startIndex);
    }
  }

  private Token scanIdentifierOrKeyword(int startIndex, char firstChar) {
    buffer.setLength(0);
    buffer.append(firstChar);
    while (!atEnd() && isIdentifierPartChar(peekChar())) {
      buffer.append(nextChar());
    }
    String value = buffer.toString();
    TokenKind keyword = TokenKind.getKeyword(value);
    if (keyword != null) {
      return createToken(keyword, startIndex);
    }
    return new IdentifierToken(getRange(startIndex), value);
  }

  private Token scanNumber(int startIndex, char firstDigit) {
    buffer.setLength(0);
    buffer.append(firstDigit);
    while (isDigit(peekChar())) {
      buffer.append(nextChar());
    }
    int value;
    try {
      value = Integer.parseInt(buffer.toString());
    } catch (NumberFormatException e) {
      reportError(startIndex, "Integer literal too large.");
      value = 0;
    }
    return new NumericLiteralToken(getRange(startIndex), value);
  }

  private Token scanStringLiteral(int startIndex) {
    buffer.setLength(0);
    while (!atEnd()) {
      char ch = nextChar();
      switch (ch) {
      case '\\':
        buffer.append(scanCharacterEscapeSequence());
        break;
      case '\"':
        return new StringLiteralToken(this.getRange(startIndex), buffer.toString());
      default:
        buffer.append(ch);
        break;
      }
    }
    reportError(startIndex, "Unterminated string literal");
    return new StringLiteralToken(this.getRange(startIndex), buffer.toString());
  }

  private char scanCharacterEscapeSequence() {
    int startIndex = index;
    char ch = nextChar();
    switch (ch) {
    case '\\': return '\\';
    case '\'': return '\'';
    case '\"': return '\"';
    case '0': return '\0';
    case 'n': return '\n';
    case 'r': return '\r';
    case 't': return '\t';
    case 'x': return scanHexEscapeDigits(startIndex);
    default:
      reportError(startIndex, "Unrecognized character escape '%s'.", ch);
      return ch;
    }
  }

  private char scanHexEscapeDigits(int startIndex) {
    if (!isHexDigit(peekChar())) {
      reportError(startIndex, "Missing hex digit in hex escape sequence.");
      return '\0';
    }
    int digits = 0;
    int value = 0;
    while (digits < 4 && isHexDigit(peekChar())) {
      digits++;
      value *= 16;
      value += hexDigitToValue(nextChar());
    }
    return (char) value;
  }

  private void skipWhitespaceAndComments() {
    while (!atEnd()) {
      skipWhitespace();
      if (!skipComment()) {
        break;
      }
    }
  }

  private boolean skipComment() {
    if (peek('/')) {
      switch (peekChar(1)) {
      case '/':
        scanSingleLineComment();
        return true;
      case '*':
        scanMultiLineComment();
        return true;
      default:
        return false;
      }
    }
    return false;
  }

  private void scanMultiLineComment() {
    int startIndex = index;
    // skip the /*
    nextChar();
    nextChar();
    while (!atEnd()) {
      if (peek('*') && peek('/', 1)) {
        // skip the */
        nextChar();
        nextChar();
        return;
      }
      nextChar();
    }
    reportError(startIndex, "Unterminated multi-line comment.");
  }

  private void scanSingleLineComment() {
    // skip the //
    nextChar();
    nextChar();
    while (!atEnd() && !isLineTerminator(nextChar())) {
      //nothing
    }
  }

  private void skipWhitespace() {
    while (Character.isWhitespace(peekChar())) {
      nextChar();
    }
  }

  private void reportError(int index, String format, Object... args) {
    this.reporter.reportError(getLocation(index), format, args);
  }

  private Token createToken(TokenKind kind, int start) {

    return new Token(kind, getRange(start));
  }

  private SourceRange getRange(int start) {
    return getRange(start, index);
  }

  private SourceRange getRange(int start, int end) {
    return new SourceRange(getLocation(start), getLocation(end));
  }

  private SourceLocation getLocation(int index) {
    return new SourceLocation(file, index);
  }

  private boolean eatOpt(char expected) {
    if (peekChar() == expected) {
      nextChar();
      return true;
    }
    return false;
  }

  private char nextChar() {
    char ch = peekChar();
    index++;
    return ch;
  }

  private boolean peek(char ch, int index) {
    return peekChar(index) == ch;
  }

  private boolean peek(char ch) {
    return peek(ch, 0);
  }

  private char peekChar(int index) {
    int offset = index + this.index;
    if (offset >= file.length()) {
      return '\0';
    }
    return file.contents.charAt(offset);
  }

  private char peekChar() {
    return peekChar(0);
  }

  private boolean atEnd() {
    return index >= file.length();
  }

  private boolean isIdentifierStartChar(char ch) {
    return Character.isLetter(ch) || ch == '_' || ch == '$';
  }

  private boolean isIdentifierPartChar(char ch) {
    return isIdentifierStartChar(ch) || isDigit(ch);
  }

  private boolean isHexDigit(char ch) {
    switch (ch) {
    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
      return true;
    default:
      return false;
    }
  }

  private int hexDigitToValue(char ch) {
    switch (ch) {
    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
      return digitToValue(ch);
    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
      return 10 + (ch - 'a');
    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
      return 10 + (ch - 'A');
    default:
      throw new RuntimeException();
    }
  }

  private int digitToValue(char digit) {
    return digit - '0';
  }

  private static boolean isDigit(char ch) {
    switch (ch) {
    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
      return true;
    default:
      return false;
    }
  }

  private static boolean isLineTerminator(char ch) {
    switch (ch) {
    case '\n':
    case '\r':
    case '\u2028':
    case '\u2029':
      return true;
    default:
      return false;
    }
  }
}
