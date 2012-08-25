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

  // Parse Class Members
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

  // Statements
  private BlockTree parseBlock() {
    Token start = peek();
    eat(TokenKind.OPEN_CURLY);
    ImmutableList<ParseTree> statements = parseStatementList();
    eat(TokenKind.CLOSE_CURLY);
    return new BlockTree(getRange(start), statements);
  }

  private ImmutableList<ParseTree> parseStatementList() {
    ImmutableList.Builder<ParseTree> statements = new ImmutableList.Builder<ParseTree>();
    while (peekStatement()) {
      statements.add(parseStatement());
    }
    return statements.build();
  }

  private ParseTree parseStatement() {
    switch (peekKind()) {
    // expression
    case OPEN_PAREN:
    case OPEN_SQUARE:
    case NULL:
    case THIS:
    case TRUE:
    case FALSE:
    case IDENTIFIER:
    case NUMBER:
    case STRING:
    case NEW:
    case TYPEOF:
    case VOID:
    case PLUS_PLUS:
    case MINUS_MINUS:
    case PLUS:
    case MINUS:
    case BANG:
    case TILDE:
      return parseExpressionStatement();

    // statements
    case VAR:
      return parseVariableStatement();
    case SEMI_COLON:
      return parseEmptyStatement();
    case IF:
      return parseIfStatement();
    case DO:
      return parseDoStatement();
    case WHILE:
      return parseWhileStatement();
    case FOR:
      return parseForStatement();
    case CONTINUE:
      return parseContinueStatement();
    case BREAK:
      return parseBreakStatement();
    case RETURN:
      return parseReturnStatement();
    case SWITCH:
      return parseSwitchStatement();
    case THROW:
      return parseThrowStatement();
    case TRY:
      return parseTryStatement();
    case DEBUGGER:
      return parseDebuggerStatement();
    default:
      throw new RuntimeException("Unexpected statement token.");
    }
  }

  private ParseTree parseVariableStatement() {
    Token start = peek();
    eat(TokenKind.VAR);
    ImmutableList<ParseTree> declarations = parseVariableDeclarations();
    eat(TokenKind.SEMI_COLON);
    return new VariableStatementTree(getRange(start), declarations);
  }

  private ImmutableList<ParseTree> parseVariableDeclarations() {
    return parseRemainingVariableDeclarations(parseVariableDeclaration());
  }

  private ImmutableList<ParseTree> parseRemainingVariableDeclarations(ParseTree element) {
    ImmutableList.Builder<ParseTree> declarations = new ImmutableList.Builder<ParseTree>();
    declarations.add(element);
    while (eatOpt(TokenKind.COMMA)) {
      declarations.add(parseVariableDeclaration());
    }
    return declarations.build();
  }

  private ParseTree parseVariableDeclaration() {
    Token start = peek();
    IdentifierToken identifier = eatId();
    ParseTree initializer = null;
    if (eatOpt(TokenKind.EQUAL)) {
      initializer = parseExpression();
    }
    return new VariableDeclarationTree(getRange(start), identifier, initializer);
  }

  private ParseTree parseEmptyStatement() {
    Token start = peek();
    eat(TokenKind.SEMI_COLON);
    return new EmptyStatementTree(getRange(start));
  }

  private ParseTree parseIfStatement() {
    Token start = peek();
    eat(TokenKind.IF);
    eat(TokenKind.OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    ParseTree ifBody = parseStatement();
    ParseTree elseBody = null;
    if (eatOpt(TokenKind.ELSE)) {
      elseBody = parseStatement();
    }
    return new IfStatementTree(getRange(start), expression, ifBody, elseBody);
  }

  private ParseTree parseDoStatement() {
    Token start = peek();
    eat(TokenKind.DO);
    ParseTree body = parseStatement();
    eat(TokenKind.WHILE);
    eat(TokenKind.OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    eat(TokenKind.SEMI_COLON);
    return new DoStatementTree(getRange(start), body, expression);
  }

  private ParseTree parseWhileStatement() {
    Token start = peek();
    eat(TokenKind.WHILE);
    eat(TokenKind.OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new WhileStatementTree(getRange(start), expression, parseStatement());
  }

  private ParseTree parseForStatement() {
    Token start = peek();
    eat(TokenKind.FOR);
    eat(TokenKind.OPEN_PAREN);
    switch (peekKind()) {
    case VAR:
      Token variableStart = peek();
      eat(TokenKind.VAR);
      ParseTree variableDeclaration = parseVariableDeclaration();
      if (eatOpt(TokenKind.IN)) {
        return parseForIn(start, variableDeclaration);
      } else {
        return parseForStatement(start,
            new VariableStatementTree(getRange(variableStart), parseRemainingVariableDeclarations(variableDeclaration)));
      }
    case SEMI_COLON:
      eat(TokenKind.SEMI_COLON);
      return parseForStatement(start, null);
    default:
      ParseTree initializer = parseExpression();
      if (eatOpt(TokenKind.IN)) {
        return parseForIn(start, initializer);
      } else {
        return parseForStatement(start, initializer);
      }
    }
  }

  private ParseTree parseForStatement(Token start, ParseTree initializer) {
    eat(TokenKind.SEMI_COLON);
    ParseTree condition = peek(TokenKind.SEMI_COLON) ? null : parseExpression();
    ParseTree increment = peek(TokenKind.CLOSE_PAREN) ? null : parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new ForStatementTree(getRange(start), initializer, condition, increment, parseStatement());
  }

  private ParseTree parseForIn(Token start, ParseTree variableDeclaration) {
    ParseTree collection = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new ForInStatementTree(getRange(start), variableDeclaration, collection, parseStatement());
  }

  private ParseTree parseContinueStatement() {
    Token start = peek();
    eat(TokenKind.CONTINUE);
    IdentifierToken label = eatIdOpt();
    eat(TokenKind.SEMI_COLON);
    return new ContinueStatementTree(getRange(start), label);
  }

  private ParseTree parseBreakStatement() {
    Token start = peek();
    eat(TokenKind.BREAK);
    IdentifierToken label = eatIdOpt();
    eat(TokenKind.SEMI_COLON);
    return new BreakStatementTree(getRange(start), label);
  }

  private ParseTree parseReturnStatement() {
    Token start = peek();
    eat(TokenKind.RETURN);
    ParseTree expression = parseExpression();
    eat(TokenKind.SEMI_COLON);
    return new ReturnStatementTree(getRange(start), expression);
  }

  private ParseTree parseSwitchStatement() {
    Token start = peek();
    eat(TokenKind.SWITCH);
    eat(TokenKind.OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    eat(TokenKind.OPEN_CURLY);
    ImmutableList.Builder<ParseTree> caseClauses = new ImmutableList.Builder<ParseTree>();
    while (peekCaseClause()) {
      caseClauses.add(parseCaseClause());
    }
    eat(TokenKind.CLOSE_CURLY);
    return new SwitchStatementTree(getRange(start), expression, caseClauses.build());
  }

  private ParseTree parseCaseClause() {
    switch (peekKind()) {
    case CASE:
      return parseCase();
    case DEFAULT:
      return parseDefault();
    default:
      throw new RuntimeException("Unexpected case clause.");
    }
  }

  private ParseTree parseDefault() {
    Token start = peek();
    eat(TokenKind.DEFAULT);
    eat(TokenKind.COLON);
    return new DefaultClauseTree(getRange(start), parseStatementList());
  }

  private ParseTree parseCase() {
    Token start = peek();
    ParseTree expression = parseExpression();
    eat(TokenKind.COLON);
    return new CaseClauseTree(getRange(start), expression, parseStatementList());
  }

  private boolean peekCaseClause() {
    switch (peekKind()) {
    case CASE:
    case DEFAULT:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseThrowStatement() {
    Token start = peek();
    eat(TokenKind.THROW);
    ParseTree exception = parseExpression();
    eat(TokenKind.SEMI_COLON);
    return new ThrowStatementTree(getRange(start), exception);
  }

  private ParseTree parseTryStatement() {
    Token start = peek();
    eat(TokenKind.TRY);
    BlockTree body = parseBlock();
    CatchClauseTree catchClause = null;
    if (peek(TokenKind.CATCH)) {
      catchClause = parseCatchClause();
    }
    BlockTree finallyBlock = null;
    if (eatOpt(TokenKind.FINALLY)) {
      finallyBlock = parseBlock();
    }
    return new TryStatementTree(getRange(start), body, catchClause, finallyBlock);
  }

  private CatchClauseTree parseCatchClause() {
    Token start = peek();
    eat(TokenKind.CATCH);
    eat(TokenKind.OPEN_PAREN);
    IdentifierToken exception = eatId();
    eat(TokenKind.CLOSE_PAREN);
    BlockTree body = parseBlock();
    return new CatchClauseTree(getRange(start), exception, body);
  }

  private ParseTree parseDebuggerStatement() {
    Token start = peek();
    eat(TokenKind.DEBUGGER);
    eat(TokenKind.SEMI_COLON);
    return new DebuggerStatementTree(getRange(start));
  }

  private ParseTree parseExpressionStatement() {
    Token start = peek();
    ParseTree expression = parseExpression();
    eat(TokenKind.SEMI_COLON);
    return new ExpressionStatementTree(getRange(start), expression);
  }

  private boolean peekStatement() {
    switch (peekKind()) {
    // expression
    case OPEN_PAREN:
    case OPEN_SQUARE:
    case NULL:
    case THIS:
    case TRUE:
    case FALSE:
    case IDENTIFIER:
    case NUMBER:
    case STRING:
    case NEW:
    case TYPEOF:
    case VOID:
    case PLUS_PLUS:
    case MINUS_MINUS:
    case PLUS:
    case MINUS:
    case BANG:
    case TILDE:
      return true;

    // statements
    case VAR:
    case SEMI_COLON:
    case IF:
    case DO:
    case WHILE:
    case FOR:
    case CONTINUE:
    case BREAK:
    case RETURN:
    case SWITCH:
    case THROW:
    case TRY:
    case DEBUGGER:
      return true;
    default:
      return false;
    }
  }

  // Parse Expressions
  private ParseTree parseExpression() {
    return parsePostfixExpression();
  }

  private ParseTree parsePrimaryExpression() {
    switch (peekKind()) {
    case IDENTIFIER:
      return parseIdentifier();
    case NUMBER:
    case STRING:
      return parseLiteral();
    default:
      reportError(nextToken(), "Expected expression.");
      return null;
    }
  }

  private ParseTree parseIdentifier() {
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

  private IdentifierToken eatId() {
    return eat(TokenKind.IDENTIFIER).asIdentifier();
  }

  private IdentifierToken eatIdOpt() {
    if (peek(TokenKind.IDENTIFIER)) {
      return eatId();
    }
    return null;
  }

}
