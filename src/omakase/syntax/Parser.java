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
public class Parser extends ParserBase {
  public Parser(ErrorReporter reporter, SourceFile file) {
    super(reporter, file, new Scanner(reporter, file));
  }

  public static ParseTree parse(ErrorReporter reporter, SourceFile file) {
    return new Parser(reporter, file).parseFile();
  }

  private ParseTree parseFile() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> declarations = new ImmutableList.Builder<ParseTree>();
    while (peekClass()) {
      declarations.add(parseClass());
    }
    eat(TokenKind.END_OF_FILE);

    return new SourceFileTree(getRange(start), declarations.build());
  }

  private boolean peekClass() {
    return peek(TokenKind.CLASS);
  }

  private ParseTree parseClass() {
    Token start = eat(TokenKind.CLASS);
    IdentifierToken name = eatId();
    eat(TokenKind.OPEN_CURLY);
    ImmutableList<ParseTree> members = parseClassMembers();
    eat(TokenKind.CLOSE_CURLY);
    return new ClassDeclarationTree(getRange(start), name, members);
  }

  private ImmutableList<ParseTree> parseClassMembers() {
    ImmutableList.Builder<ParseTree> members = new ImmutableList.Builder<ParseTree>();
    while (peekClassMember()) {
      members.add(parseClassMember());
    }
    return members.build();
  }

  private ParseTree parseClassMember() {
    boolean isNative = eatOpt(TokenKind.NATIVE);
    IdentifierToken name = eatId();
    ImmutableList<ParseTree> formals = parseParameterListDeclaration();
    ParseTree body = parseBlock(isNative);
    return new MethodDeclarationTree(getRange(name), name, formals, isNative, body);
  }

  private ParseTree parseBlock(boolean isNative) {
    return isNative ? parseNativeBlock() : parseBlock();
  }

  private ParseTree parseNativeBlock() {
    JavascriptParser nativeParser = new JavascriptParser(reporter,
        new SourceRange(this.file(), this.getPosition(), this.file().length()));
    ParseTree result = nativeParser.parseBlock();
    this.setPosition(nativeParser.getPosition());
    return result;
  }

  private BlockTree parseBlock() {
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
    eat(TokenKind.SEMI_COLON);
    return new ExpressionStatementTree(getRange(start), expression);
  }

  private ParseTree parseExpression() {
    return parsePostfixExpression();
  }

  private ParseTree parsePrimaryExpression() {
    switch (peekKind()) {
    case IDENTIFIER:
      return parseSimpleName();
    case NUMBER:
    case STRING:
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
      case OPEN_PAREN:
        primary = parseCallExpression(primary);
      }
    }
    return primary;
  }

  private ParseTree parseCallExpression(ParseTree primary) {
    ImmutableList.Builder<ParseTree> arguments = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.OPEN_PAREN);
    if (peekExpression()) {
      arguments.add(parseExpression());
      while (eatOpt(TokenKind.COMMA)) {
        arguments.add(parseExpression());
      }
    }
    eat(TokenKind.CLOSE_PAREN);
    return new CallExpressionTree(getRange(primary.start()), primary, arguments.build());
  }

  private boolean peekPostfixOperator() {
    switch (peekKind()) {
    case OPEN_PAREN:
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
    case IDENTIFIER:
    case NUMBER:
    case STRING:
    // TODO: others
      return true;
    default:
      return false;
    }
  }

  private ImmutableList<ParseTree> parseParameterListDeclaration() {
    ImmutableList.Builder<ParseTree> result = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.OPEN_PAREN);
    if (peekParameter()) {
      result.add(parseParameter());
      while (eatOpt(TokenKind.COMMA)) {
        result.add(parseParameter());
      }
    }
    eat(TokenKind.CLOSE_PAREN);
    return result.build();
  }

  private ParseTree parseParameter() {
    IdentifierToken name = eatId();
    return new ParameterDeclarationTree(getRange(name), name);
  }

  private boolean peekParameter() {
    return peek(TokenKind.IDENTIFIER);
  }

  private boolean peekClassMember() {
    switch (peekKind()) {
    case IDENTIFIER:
    case NATIVE:
      return true;
    }
    return false;
  }

  private IdentifierToken eatId() {
    return eat(TokenKind.IDENTIFIER).asIdentifier();
  }

}
