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
import omakase.syntax.tokens.IdentifierToken;
import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.trees.*;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;
import omakase.util.SourceRange;

/**
 *
 */
public class JavascriptParser extends ParserBase {
  private final JavascriptScanner scanner;

  public JavascriptParser(ErrorReporter reporter, SourceRange source) {
    super(reporter, source.file(), new JavascriptScanner(reporter, source));
    this.scanner = (JavascriptScanner) super.scanner;
  }

  public BlockTree parseBlock() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> statements = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.OPEN_CURLY);
    while (peekStatement()) {
      statements.add(parseStatement());
    }
    eat(TokenKind.CLOSE_CURLY);
    return new BlockTree(getRange(start), statements.build());
  }

  private ParseTree parseStatement() {
    // TODO: Other statements.
    return parseExpressionStatement();
  }

  private ParseTree parseExpressionStatement() {
    Token start = peek();
    ParseTree expression = parseExpression();
    eat(TokenKind.JAVASCRIPT_SEMI_COLON);
    return new ExpressionStatementTree(getRange(start), expression);
  }

  private ParseTree parseExpression() {
    return parsePostfixExpression();
  }

  private ParseTree parsePrimaryExpression() {
    switch (peekKind()) {
    case JAVASCRIPT_IDENTIFIER:
      return parseSimpleName();
    case JAVASCRIPT_NUMBER:
    case JAVASCRIPT_STRING:
      return parseLiteral();
    default:
      reportError(nextToken(), "Expected expression.");
      return null;
    }
  }

  private ParseTree parseSimpleName() {
    IdentifierToken name = eatId();
    return new SimpleNameExpressionTree(getRange(name), name);
  }

  private ParseTree parsePostfixExpression() {
    ParseTree primary = parsePrimaryExpression();
    while (peekPostfixOperator()) {
      switch(peekKind()) {
      case JAVASCRIPT_OPEN_PAREN:
        primary = parseCallExpression(primary);
      }
    }
    return primary;
  }

  private ParseTree parseCallExpression(ParseTree primary) {
    ImmutableList.Builder<ParseTree> arguments = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.JAVASCRIPT_OPEN_PAREN);
    if (peekExpression()) {
      arguments.add(parseExpression());
      while (eatOpt(TokenKind.JAVASCRIPT_COMMA)) {
        arguments.add(parseExpression());
      }
    }
    eat(TokenKind.JAVASCRIPT_CLOSE_PAREN);
    return new CallExpressionTree(getRange(primary.start()), primary, arguments.build());
  }

  private boolean peekPostfixOperator() {
    switch (peekKind()) {
    case JAVASCRIPT_OPEN_PAREN:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseLiteral() {
    Token value = nextToken();
    return new LiteralExpressionTree(getRange(value), value);
  }

  private boolean peekStatement() {
    return peekExpression();
  }

  private boolean peekExpression() {
    switch (peekKind()) {
    case JAVASCRIPT_IDENTIFIER:
    case JAVASCRIPT_NUMBER:
    case JAVASCRIPT_STRING:
      // TODO: others
      return true;
    default:
      return false;
    }
  }

  private ImmutableList<ParseTree> parseParameterListDeclaration() {
    ImmutableList.Builder<ParseTree> result = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.JAVASCRIPT_OPEN_PAREN);
    if (peekParameter()) {
      result.add(parseParameter());
      while (eatOpt(TokenKind.JAVASCRIPT_COMMA)) {
        result.add(parseParameter());
      }
    }
    eat(TokenKind.JAVASCRIPT_CLOSE_PAREN);
    return result.build();
  }

  private ParseTree parseParameter() {
    IdentifierToken name = eatId();
    return new ParameterDeclarationTree(getRange(name), name);
  }

  private boolean peekParameter() {
    return peek(TokenKind.JAVASCRIPT_IDENTIFIER);
  }

  private IdentifierToken eatId() {
    return eat(TokenKind.JAVASCRIPT_IDENTIFIER).asIdentifier();
  }
}
