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

import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
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

  /**
   * Report an error.
   * @param token The token in the source file to report the error at.
   * @param format A format message to report.
   * @param args The arguments of the format.
   */
  protected void reportError(Token token, String format, Object... args) {
    this.reporter.reportError(token.start(), format, args);
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
      reportError(result, "%s expected.", kind);
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
    return peek().kind == kind;
  }

  protected TokenKind peekKind() {
    return peek().kind;
  }

  private Token peek(int offset) {
    while (offset >= tokens.size()) {
      tokens.add(scanner.scanToken());
    }
    return tokens.get(offset);
  }

  private int getPosition() {
    if (tokens.size() > 0) {
      return tokens.get(0).start().offset;
    } else {
      return scanner.getPosition();
    }
  }

  private void setPosition(int newPosition) {
    if (newPosition != getPosition()) {
      tokens.clear();
      scanner.setPosition(newPosition);
    }
  }

  protected Token peek() {
    return peek(0);
  }
}
