// Copyright 2012 Peter Hallam
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
import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.trees.ParseTree;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;
import omakase.util.SourceLocation;
import omakase.util.SourceRange;

import java.util.ArrayList;

/**
 */
public class ParserBase {
  protected final ErrorReporter reporter;
  protected final ArrayList<Token> tokens;
  protected final ScannerBase scanner;
  private Token lastToken = null;

  public ParserBase(ErrorReporter reporter, SourceFile file, ScannerBase scanner) {
    this.tokens = new ArrayList<Token>(5);
    this.scanner = scanner;
    this.reporter = reporter;
  }

  public interface PeekFunction {
    boolean peek();
  }

  public interface ParseFunction {
    ParseTree parse();
  }

  public interface ParseListFunction {
    ImmutableList<ParseTree> parse();
  }

  protected ImmutableList<ParseTree> parseList(PeekFunction peek, ParseFunction parse) {
    var elements = new ImmutableList.Builder<ParseTree>();
    while (peek.peek()) {
      elements.add(parse.parse());
    }
    return elements.build();
  }

  protected ImmutableList<ParseTree> parseSeparatedList(
      TokenKind separator,
      ParseFunction parse
  ) {
    var elements = new ImmutableList.Builder<ParseTree>();
    do {
      elements.add(parse.parse());
    } while (eatOpt(separator));
    return elements.build();
  }

  protected ImmutableList<ParseTree> parseCommaSeparatedList(ParseFunction parse) {
    return parseSeparatedList(TokenKind.COMMA, parse);
  }

  protected ImmutableList<ParseTree> parseCommaSeparatedListOpt(
      PeekFunction peek,
      ParseFunction parse
  ) {
    if (peek.peek()) {
      return parseCommaSeparatedList(parse);
    } else {
      return ImmutableList.of();
    }
  }

  protected ImmutableList<ParseTree> parseDelimitedList(
      TokenKind startDelimiter,
      ParseListFunction parse,
      TokenKind endDelimiter
  ) {
    eat(startDelimiter);
    var result = parse.parse();
    eat(endDelimiter);
    return result;
  }

  protected ImmutableList<ParseTree> parseParenList(
      ParseListFunction parse
  ) {
    return parseDelimitedList(TokenKind.OPEN_PAREN, parse, TokenKind.CLOSE_PAREN);
  }

  /**
   * Report an error.
   * @param token The token in the source file to report the error at.
   * @param format A format message to report.
   * @param args The arguments of the format.
   */
  protected void reportError(Token token, String format, Object... args) {
    SourceLocation location = token.start();
    reportError(location, format, args);
  }

  protected void reportError(SourceLocation location, String format, Object... args) {
    this.reporter.reportError(location, format, args);
  }

  protected SourceRange getRange(Token startToken) {
    return getRange(startToken.start());
  }

  protected SourceRange getRange(SourceLocation start) {
    SourceLocation end = (lastToken != null) ? lastToken.end() : peek().start();
    return new SourceRange(start, end);
  }

  protected Token eat(TokenKind kind) {
    Token result = nextToken();
    if (result.kind != kind) {
      reportError(result, "%s expected.", kind.value());
    }
    return result;
  }

  protected boolean eatOpt(TokenKind kind) {
    if (peek(kind)) {
      nextToken();
      return true;
    }
    return false;
  }

  protected Token nextToken() {
    this.lastToken = peek();
    tokens.remove(0);
    return this.lastToken;
  }

  protected boolean peek(TokenKind kind) {
    return peek(0, kind);
  }

  protected boolean peek(int index, TokenKind kind) {
    return peek(index).kind == kind;
  }

  protected TokenKind peekKind() {
    return peekKind(0);
  }

  protected TokenKind peekKind(int index) {
      return peek(index).kind;
  }

  private Token peek(int offset) {
    while (offset >= tokens.size()) {
      tokens.add(scanner.scanToken());
    }
    return tokens.get(offset);
  }

  protected int getPosition() {
    if (tokens.size() > 0) {
      return tokens.get(0).start().offset;
    } else {
      return scanner.getPosition();
    }
  }

  protected void setPosition(int newPosition) {
    if (newPosition != getPosition()) {
      tokens.clear();
      scanner.setPosition(newPosition);
    }
  }

  protected Token peek() {
    return peek(0);
  }

  protected SourceFile file() {
    return scanner.file;
  }
}
