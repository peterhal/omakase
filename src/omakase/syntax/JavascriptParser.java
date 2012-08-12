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
import omakase.syntax.tokens.javascript.*;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.javascript.*;
import omakase.util.ErrorReporter;
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
    eat(TokenKind.JS_OPEN_CURLY);
    while (peekStatement()) {
      statements.add(parseStatement());
    }
    eat(TokenKind.JS_CLOSE_CURLY);
    return new BlockTree(getRange(start), statements.build());
  }

  public ProgramTree parseProgram() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> elements = new ImmutableList.Builder<ParseTree>();
    while (peekSourceElement()) {
      elements.add(parseSourceElement());
    }
    return new ProgramTree(getRange(start), elements.build());
  }

  private ParseTree parseSourceElement() {
    if (peekFunction()) {
      return parseFunction();
    } else {
      return parseStatement();
    }
  }

  private ParseTree parseFunction() {
    Token start = peek();
    eat(TokenKind.JS_FUNCTION);
    IdentifierToken id = eatOptId();

    return new FunctionExpressionTree(getRange(start), id, parseFormalParameterList(), parseBlock());
  }

  private FormalParameterListTree parseFormalParameterList() {
    Token start = peek();
    ImmutableList.Builder<IdentifierToken> parameters = new ImmutableList.Builder<IdentifierToken>();
    eat(TokenKind.JS_OPEN_PAREN);
    if (peekParameter()) {
      parameters.add(eatId());
      while (eatOpt(TokenKind.JS_COMMA)) {
        parameters.add(eatId());
      }
    }
    eat(TokenKind.JS_CLOSE_PAREN);
    return new FormalParameterListTree(getRange(start), parameters.build());
  }

  private boolean peekSourceElement() {
    return peekFunction() || peekStatement();
  }

  private boolean peekFunction() {
    return peek(TokenKind.JS_FUNCTION);
  }

  private ParseTree parseStatement() {
    // TODO: Other statements.
    return parseExpressionStatement();
  }

  private ParseTree parseExpressionStatement() {
    Token start = peek();
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_SEMI_COLON);
    return new ExpressionStatementTree(getRange(start), expression);
  }

  private ParseTree parseExpression() {
    return parsePostfixExpression();
  }

  private ParseTree parsePrimaryExpression() {
    switch (peekKind()) {
    case JS_IDENTIFIER:
      return parseSimpleName();
    case JS_NUMBER:
    case JS_STRING:
      return parseLiteral();
    default:
      reportError(nextToken(), "Expected expression.");
      return null;
    }
  }

  private ParseTree parseSimpleName() {
    IdentifierToken name = eatId();
    return new IdentifierExpressionTree(getRange(name), name);
  }

  private ParseTree parsePostfixExpression() {
    ParseTree primary = parsePrimaryExpression();
    while (peekPostfixOperator()) {
      switch(peekKind()) {
      case JS_OPEN_PAREN:
        primary = parseCallExpression(primary);
      }
    }
    return primary;
  }

  private ParseTree parseCallExpression(ParseTree primary) {
    ImmutableList.Builder<ParseTree> arguments = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.JS_OPEN_PAREN);
    if (peekExpression()) {
      arguments.add(parseExpression());
      while (eatOpt(TokenKind.JS_COMMA)) {
        arguments.add(parseExpression());
      }
    }
    eat(TokenKind.JS_CLOSE_PAREN);
    return new CallExpressionTree(getRange(primary.start()), primary, arguments.build());
  }

  private boolean peekPostfixOperator() {
    switch (peekKind()) {
    case JS_OPEN_PAREN:
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
    case JS_IDENTIFIER:
    case JS_NUMBER:
    case JS_STRING:
      // TODO: others
      return true;
    default:
      return false;
    }
  }

  private boolean peekParameter() {
    return peek(TokenKind.JS_IDENTIFIER);
  }

  private IdentifierToken eatId() {
    return eat(TokenKind.JS_IDENTIFIER).asJavascriptIdentifier();
  }

  private IdentifierToken eatOptId() {
    if (peek(TokenKind.JS_IDENTIFIER)) {
      return eatId();
    }
    return null;
  }
}
