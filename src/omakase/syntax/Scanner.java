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
import omakase.util.*;
import static omakase.util.Characters.*;

/**
 * Scanning is the first phase of compilation. The scanner takes the raw characters of a source
 * file and converts them to tokens. Tokens include punctuation, identifiers, keywords and literals.
 * While scanning for tokens, whitespace and comments are ignored.
 */
public class Scanner extends ScannerBase {
  /**
   * @param reporter Where to report errors during scanning.
   * @param file The file to scan.
   */
  public Scanner(ErrorReporter reporter, SourceFile file) {
    super(new SourceRange(file), reporter);
  }

  /**
   * Scans a single token.
   * @return The token scanned.
   */
  public Token scanToken() {
    skipWhitespaceAndComments();
    int startIndex = index;
    char ch = nextChar();
    switch (ch) {
    case '\0': return createToken(TokenKind.END_OF_FILE, startIndex);
    case '{': return createToken(TokenKind.OPEN_CURLY, startIndex);
    case '}': return createToken(TokenKind.CLOSE_CURLY, startIndex);
    case '(': return createToken(TokenKind.OPEN_PAREN, startIndex);
    case ')': return createToken(TokenKind.CLOSE_PAREN, startIndex);
    case '[': return createToken(TokenKind.OPEN_SQUARE, startIndex);
    case ']': return createToken(TokenKind.CLOSE_SQUARE, startIndex);
    case '.': return createToken(TokenKind.PERIOD, startIndex);
    case ';': return createToken(TokenKind.SEMI_COLON, startIndex);
    case ',': return createToken(TokenKind.COMMA, startIndex);
    case '~': return createToken(TokenKind.TILDE, startIndex);
    case '<':
      if (eatOpt('=')) {
        return createToken(TokenKind.LESS_EQUAL, startIndex);
      }
      if (eatOpt('<')) {
        if (eatOpt('=')) {
          return createToken(TokenKind.LEFT_SHIFT_EQUAL, startIndex);
        }
        return createToken(TokenKind.SHIFT_LEFT, startIndex);
      }
      return createToken(TokenKind.OPEN_ANGLE, startIndex);
    case '>':
      if (eatOpt('=')) {
        return createToken(TokenKind.GREATER_EQUAL, startIndex);
      }
      if (eatOpt('>')) {
        if (eatOpt('=')) {
          return createToken(TokenKind.RIGHT_SHIFT_EQUAL, startIndex);
        }
        return createToken(TokenKind.SHIFT_RIGHT, startIndex);
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
    case '+':
      if (eatOpt('+')) {
        return createToken(TokenKind.PLUS_PLUS, startIndex);
      }
      if (eatOpt('=')) {
        return createToken(TokenKind.PLUS_EQUAL, startIndex);
      }
      return createToken(TokenKind.PLUS, startIndex);
    case '-':
      if (eatOpt('-')) {
        return createToken(TokenKind.MINUS_MINUS, startIndex);
      }
      if (eatOpt('=')) {
        return createToken(TokenKind.MINUS_EQUAL, startIndex);
      }
      if (eatOpt('>')) {
        return createToken(TokenKind.ARROW, startIndex);
      }
      return createToken(TokenKind.MINUS, startIndex);
    case '*':
      if (eatOpt('=')) {
        return createToken(TokenKind.STAR_EQUAL, startIndex);
      }
      return createToken(TokenKind.STAR, startIndex);
    case '%':
      if (eatOpt('=')) {
        return createToken(TokenKind.PERCENT_EQUAL, startIndex);
      }
      return createToken(TokenKind.PERCENT, startIndex);
    case '^':
      if (eatOpt('=')) {
        return createToken(TokenKind.HAT_EQUAL, startIndex);
      }
      return createToken(TokenKind.HAT, startIndex);
    case '&':
      if (eatOpt('&')) {
        return createToken(TokenKind.AMPERSAND_AMPERSAND, startIndex);
      }
      if (eatOpt('=')) {
        return createToken(TokenKind.AMPERSAND_EQUAL, startIndex);
      }
      return createToken(TokenKind.AMPERSAND, startIndex);
    case '|':
      if (eatOpt('|')) {
        return createToken(TokenKind.BAR_BAR, startIndex);
      }
      if (eatOpt('=')) {
        return createToken(TokenKind.BAR_EQUAL, startIndex);
      }
      return createToken(TokenKind.BAR, startIndex);
    case '?': return createToken(TokenKind.QUESTION, startIndex);
    case ':': return createToken(TokenKind.COLON, startIndex);
    case '/':
      if (eatOpt('=')) {
        return createToken(TokenKind.SLASH_EQUAL, startIndex);
      }
      return createToken(TokenKind.SLASH, startIndex);
    case '\"': return scanStringLiteral(startIndex);
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

  /**
   * Scans an identifier or keyword token. Note the current position will be immediately after the
   * first character in the identifier/keyword when this method is called.
   *
   * @param startIndex The index of the first character in the token.
   * @param firstChar The first character in the token.
   * @return The token representing the identifier or keyword scanned.
   */
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

  /**
   * Scan a Numeric literal. Note that the current position will be immediately after the first
   * digit of the number when this method is called.
   * @param startIndex The index of the first digit in the number.
   * @param firstDigit The first digit in the number.
   * @return A token representing the scanned number.
   */
  private NumericLiteralToken scanNumber(int startIndex, char firstDigit) {
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

  /**
   * Scan a string literal token. Note the current position will be immediately after the opening
   * " character when this method is called.
   *
   * @param startIndex The index of the opening " character.
   * @return The token scanned.
   */
  private StringLiteralToken scanStringLiteral(int startIndex) {
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

  /**
   * Scans past an escape sequence in a string literal.
   * @return The value of the character scanned.
   */
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
    case 'x': return scanHexEscapeDigits();
    default:
      reportError(startIndex, "Unrecognized character escape '%s'.", ch);
      return ch;
    }
  }

  /**
   * Scan past up to 4 hex digits.
   * @return Return the value of the digits scanned.
   */
  private char scanHexEscapeDigits() {
    if (!isHexDigit(peekChar())) {
      reportError(index, "Missing hex digit in hex escape sequence.");
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

  /**
   * Skip forward past any amount of whitespace or comments until a non-whitespace or comment is
   * found.
   */
  private void skipWhitespaceAndComments() {
    while (!atEnd()) {
      skipWhitespace();
      if (!skipComment()) {
        break;
      }
    }
  }

  /**
   * Detect if a comment starts at the current location. If so, skip past it.
   * @return True if a comment was skipped.
   */
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

  /**
   * Skip past a multi line comment.
   */
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

  /**
   * Skip past a single line comment.
   */
  private void scanSingleLineComment() {
    // skip the //
    nextChar();
    nextChar();
    while (!atEnd() && !isLineTerminator(nextChar())) {
      //nothing
    }
  }

  /**
   * Skip past any whitespace characters.
   */
  private void skipWhitespace() {
    while (Character.isWhitespace(peekChar())) {
      nextChar();
    }
  }

  /**
   * Create a new simple token.
   * @param kind The kind of token to create.
   * @param start The starting offset of the token.
   * @return A new token from start to the current position.
   */
  private Token createToken(TokenKind kind, int start) {

    return new Token(kind, getRange(start));
  }

  public static ImmutableList<Token> scanFile(ErrorReporter reporter, SourceFile file) {
    ImmutableList.Builder<Token> tokens = new ImmutableList.Builder<Token>();
    Scanner scanner = new Scanner(reporter, file);
    do {
      tokens.add(scanner.scanToken());
    } while (!scanner.atEnd());
    return tokens.build();
  }
}
