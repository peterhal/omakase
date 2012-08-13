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
 * TODO: Automatic semi-colon insertion.
 */
public class JavascriptParser extends ParserBase {
  private final JavascriptScanner scanner;

  public JavascriptParser(ErrorReporter reporter, SourceRange source) {
    super(reporter, source.file(), new JavascriptScanner(reporter, source));
    this.scanner = (JavascriptScanner) super.scanner;
  }

  public BlockTree parseBlock() {
    Token start = peek();
    eat(TokenKind.JS_OPEN_CURLY);
    ImmutableList<ParseTree> statements = parseStatementList();
    eat(TokenKind.JS_CLOSE_CURLY);
    return new BlockTree(getRange(start), statements);
  }

  private ImmutableList<ParseTree> parseStatementList() {
    ImmutableList.Builder<ParseTree> statements = new ImmutableList.Builder<ParseTree>();
    while (peekStatement()) {
      statements.add(parseStatement());
    }
    return statements.build();
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
    switch (peekKind()) {
    // expression
    case JS_OPEN_PAREN:
    case JS_OPEN_SQUARE:
    case JS_NULL:
    case JS_THIS:
    case JS_TRUE:
    case JS_FALSE:
    case JS_IDENTIFIER:
    case JS_NUMBER:
    case JS_STRING:
    case JS_NEW:
    case JS_DELETE:
    case JS_TYPEOF:
    case JS_VOID:
    case JS_PLUS_PLUS:
    case JS_MINUS_MINUS:
    case JS_PLUS:
    case JS_MINUS:
    case JS_BANG:
    case JS_TILDE:
      return parseExpressionStatement();

    // expression or statement
    case JS_FUNCTION:
      // TODO: Ambiguity with expression statement.
      return parseFunction();
    case JS_OPEN_CURLY:
      // TODO: Ambiguity with object literal.
      return parseBlock();

      // statements
    case JS_VAR:
      return parseVariableStatement();
    case JS_SEMI_COLON:
      return parseEmptyStatement();
    case JS_IF:
      return parseIfStatement();
    case JS_DO:
      return parseDoStatement();
    case JS_WHILE:
      return parseWhileStatement();
    case JS_FOR:
      return parseForStatement();
    case JS_CONTINUE:
      return parseContinueStatement();
    case JS_BREAK:
      return parseBreakStatement();
    case JS_RETURN:
      return parseReturnStatement();
    case JS_WITH:
      return parseWithStatement();
    case JS_SWITCH:
      return parseSwitchStatement();
    case JS_THROW:
      return parseThrowStatement();
    case JS_TRY:
      return parseTryStatement();
    case JS_DEBUGGER:
      return parseDebuggerStatement();
    default:
      throw new RuntimeException("Unexpected statement token.");
    }
  }

  private ParseTree parseVariableStatement() {
    Token start = peek();
    eat(TokenKind.JS_VAR);
    ImmutableList<ParseTree> declarations = parseVariableDeclarations();
    eat(TokenKind.JS_SEMI_COLON);
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
    if (eatOpt(TokenKind.JS_EQUAL)) {
      initializer = parseExpression();
    }
    return new VariableDeclarationTree(getRange(start), identifier, initializer);
  }

  private ParseTree parseEmptyStatement() {
    Token start = peek();
    eat(TokenKind.JS_SEMI_COLON);
    return new EmptyStatementTree(getRange(start));
  }

  private ParseTree parseIfStatement() {
    Token start = peek();
    eat(TokenKind.JS_IF);
    eat(TokenKind.JS_OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    ParseTree ifBody = parseStatement();
    ParseTree elseBody = null;
    if (eatOpt(TokenKind.JS_ELSE)) {
      elseBody = parseStatement();
    }
    return new IfStatementTree(getRange(start), expression, ifBody, elseBody);
  }

  private ParseTree parseDoStatement() {
    Token start = peek();
    eat(TokenKind.JS_DO);
    ParseTree body = parseStatement();
    eat(TokenKind.JS_WHILE);
    eat(TokenKind.JS_OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    eat(TokenKind.JS_SEMI_COLON);
    return new DoStatementTree(getRange(start), body, expression);
  }

  private ParseTree parseWhileStatement() {
    Token start = peek();
    eat(TokenKind.JS_WHILE);
    eat(TokenKind.JS_OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    return new WhileStatementTree(getRange(start), expression, parseStatement());
  }

  private ParseTree parseForStatement() {
    Token start = peek();
    eat(TokenKind.JS_FOR);
    eat(TokenKind.JS_OPEN_PAREN);
    switch (peekKind()) {
    // TODO: NoIn
    case VAR:
      Token variableStart = peek();
      eat(TokenKind.VAR);
      ParseTree variableDeclaration = parseVariableDeclaration();
      if (eatOpt(TokenKind.JS_IN)) {
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
      if (eatOpt(TokenKind.JS_IN)) {
        return parseForIn(start, initializer);
      } else {
        return parseForStatement(start, initializer);
      }
    }
  }

  private ParseTree parseForStatement(Token start, ParseTree initializer) {
    eat(TokenKind.JS_SEMI_COLON);
    ParseTree condition = peek(TokenKind.JS_SEMI_COLON) ? null : parseExpression();
    ParseTree increment = peek(TokenKind.JS_CLOSE_PAREN) ? null : parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    return new ForStatementTree(getRange(start), initializer, condition, increment, parseStatement());
  }

  private ParseTree parseForIn(Token start, ParseTree variableDeclaration) {
    ParseTree collection = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    return new ForInStatementTree(getRange(start), variableDeclaration, collection, parseStatement());
  }

  private ParseTree parseContinueStatement() {
    Token start = peek();
    eat(TokenKind.JS_CONTINUE);
    eat(TokenKind.JS_SEMI_COLON);
    return new ContinueStatementTree(getRange(start));
  }

  private ParseTree parseBreakStatement() {
    Token start = peek();
    eat(TokenKind.JS_BREAK);
    eat(TokenKind.JS_SEMI_COLON);
    return new BreakStatementTree(getRange(start));
  }

  private ParseTree parseReturnStatement() {
    Token start = peek();
    eat(TokenKind.JS_RETURN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_SEMI_COLON);
    return new ReturnStatementTree(getRange(start), expression);
  }

  private ParseTree parseWithStatement() {
    Token start = peek();
    eat(TokenKind.JS_WITH);
    eat(TokenKind.JS_OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    return new WithStatementTree(getRange(start), expression, parseStatement());
  }

  private ParseTree parseSwitchStatement() {
    Token start = peek();
    eat(TokenKind.JS_SWITCH);
    eat(TokenKind.JS_OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    eat(TokenKind.JS_OPEN_CURLY);
    ImmutableList.Builder<ParseTree> caseClauses = new ImmutableList.Builder<ParseTree>();
    while (peekCaseClause()) {
      caseClauses.add(parseCaseClause());
    }
    eat(TokenKind.JS_CLOSE_CURLY);
    return new SwitchStatementTree(getRange(start), expression, caseClauses.build());
  }

  private ParseTree parseCaseClause() {
    switch (peekKind()) {
    case JS_CASE:
      return parseCase();
    case JS_DEFAULT:
      return parseDefault();
    default:
      throw new RuntimeException("Unexpected case clause.");
    }
  }

  private ParseTree parseDefault() {
    Token start = peek();
    eat(TokenKind.JS_DEFAULT);
    eat(TokenKind.JS_COLON);
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
    case JS_CASE:
    case JS_DEFAULT:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseThrowStatement() {
    Token start = peek();
    eat(TokenKind.JS_THROW);
    ParseTree exception = parseExpression();
    eat(TokenKind.SEMI_COLON);
    return new ThrowStatementTree(getRange(start), exception);
  }

  private ParseTree parseTryStatement() {
    Token start = peek();
    eat(TokenKind.JS_TRY);
    BlockTree body = parseBlock();
    CatchClauseTree catchClause = null;
    if (peek(TokenKind.JS_CATCH)) {
      catchClause = parseCatchClause();
    }
    BlockTree finallyBlock = null;
    if (eatOpt(TokenKind.JS_FINALLY)) {
      finallyBlock = parseBlock();
    }
    return new TryStatementTree(getRange(start), body, catchClause, finallyBlock);
  }

  private CatchClauseTree parseCatchClause() {
    Token start = peek();
    eat(TokenKind.JS_CATCH);
    eat(TokenKind.JS_OPEN_PAREN);
    IdentifierToken exception = eatId();
    eat(TokenKind.JS_CLOSE_PAREN);
    BlockTree body = parseBlock();
    return new CatchClauseTree(getRange(start), exception, body);
  }

  private ParseTree parseDebuggerStatement() {
    Token start = peek();
    eat(TokenKind.JS_DEBUGGER);
    eat(TokenKind.JS_SEMI_COLON);
    return new DebuggerStatementTree(getRange(start));
  }

  private ParseTree parseExpressionStatement() {
    Token start = peek();
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_SEMI_COLON);
    return new ExpressionStatementTree(getRange(start), expression);
  }

  private ParseTree parseExpression() {
    return parseExpression(Expression.NORMAL);
  }

  private ParseTree parseExpression(Expression expression) {
      return parseCommaExpression(expression);
  }

  private ParseTree parseCommaExpression(Expression expression) {
    Token start = peek();
    ParseTree assignment = parseAssignmentExpression(expression);
    if (!peek(TokenKind.COMMA)) {
      return assignment;
    }

    ImmutableList.Builder<ParseTree> expressions = new ImmutableList.Builder<ParseTree>();
    expressions.add(assignment);
    while (eatOpt(TokenKind.JS_COMMA)) {
      expressions.add(parseAssignmentExpression(expression));
    }
    return new CommaExpressionTree(getRange(start), expressions.build());
  }

  private ParseTree parseAssignmentExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseConditionalExpression(expression);
    if (!peekAssignmentOperator()) {
      return left;
    }
    // TODO: Check for LHS.
    Token operator = nextToken();
    ParseTree right = parseAssignmentExpression(expression);
    return new BinaryExpressionTree(getRange(start), left, operator, right);
  }

  private ParseTree parseConditionalExpression(Expression expression) {
    Token start = peek();
    ParseTree condition = parseLogicalOrExpression(expression);
    if (!eatOpt(TokenKind.QUESTION)) {
      return condition;
    }
    ParseTree trueCase = parseAssignmentExpression(expression);
    eat(TokenKind.COLON);
    ParseTree falseCase = parseAssignmentExpression(expression);
    return new ConditionalExpressionTree(getRange(start), condition, trueCase, falseCase);
  }

  private ParseTree parseLogicalOrExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseLogicalAndExpression(expression);
    while (peek(TokenKind.JS_BAR_BAR)) {
      Token operator = nextToken();
      ParseTree right = parseLogicalAndExpression(expression);
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseLogicalAndExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseBitwiseOrExpression(expression);
    while (peek(TokenKind.JS_AMPERSAND_AMPERSAND)) {
      Token operator = nextToken();
      ParseTree right = parseBitwiseOrExpression(expression);
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseOrExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseBitwiseXorExpression(expression);
    while (peek(TokenKind.JS_BAR)) {
      Token operator = nextToken();
      ParseTree right = parseBitwiseXorExpression(expression);
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseXorExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseBitwiseAndExpression(expression);
    while (peek(TokenKind.JS_BAR)) {
      Token operator = nextToken();
      ParseTree right = parseBitwiseAndExpression(expression);
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseAndExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseEqualityExpression(expression);
    while (peek(TokenKind.JS_BAR)) {
      Token operator = nextToken();
      ParseTree right = parseEqualityExpression(expression);
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseEqualityExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseRelationalExpression(expression);
    while (peekEqualityOperator()) {
      Token operator = nextToken();
      ParseTree right = parseRelationalExpression(expression);
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseRelationalExpression(Expression expression) {
    Token start = peek();
    ParseTree left = parseShiftExpression();
    while (peekRelationalOperator(expression)) {
      Token operator = nextToken();
      ParseTree right = parseShiftExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseShiftExpression() {
    Token start = peek();
    ParseTree left = parseAdditiveExpression();
    while (peekShiftOperator()) {
      Token operator = nextToken();
      ParseTree right = parseAdditiveExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseAdditiveExpression() {
    Token start = peek();
    ParseTree left = parseMultiplicativeExpression();
    while (peekAdditiveOperator()) {
      Token operator = nextToken();
      ParseTree right = parseMultiplicativeExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseMultiplicativeExpression() {
    Token start = peek();
    ParseTree left = parseUnaryExpression();
    while (peekMultiplicativeOperator()) {
      Token operator = nextToken();
      ParseTree right = parseUnaryExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseUnaryExpression() {
    Token start = peek();
    if (peekUnaryOperator()) {
      Token operator = nextToken();
      return new UnaryExpressionTree(getRange(start), operator, parseUnaryExpression());
    }
    return parsePostfixExpression();
  }

  private boolean peekUnaryOperator() {
    switch (peekKind()) {
    case JS_DELETE:
    case JS_VOID:
    case JS_TYPEOF:
    case JS_PLUS_PLUS:
    case JS_MINUS_MINUS:
    case JS_PLUS:
    case JS_MINUS:
    case JS_TILDE:
    case JS_BANG:
      return true;
    default:
      return false;
    }
  }

  private boolean peekMultiplicativeOperator() {
    switch (peekKind()) {
    case JS_STAR:
    case JS_SLASH:
    case JS_PERCENT:
      return true;
    default:
      return false;
    }
  }

  private boolean peekAdditiveOperator() {
    switch (peekKind()) {
    case JS_PLUS:
    case JS_MINUS:
      return true;
    default:
      return false;
    }
  }

  private boolean peekShiftOperator() {
    switch (peekKind()) {
    case JS_SHIFT_LEFT:
    case JS_SHIFT_RIGHT:
    case JS_UNSIGNED_SHIFT_RIGHT:
      return true;
    default:
      return false;
    }
  }

  private static enum Expression {
    NO_IN,
    NORMAL,
  }

  private boolean peekRelationalOperator(Expression expression) {
    switch (peekKind()) {
    case JS_OPEN_ANGLE:
    case JS_CLOSE_ANGLE:
    case JS_GREATER_EQUAL:
    case JS_LESS_EQUAL:
    case JS_INSTANCEOF:
      return true;
    case JS_IN:
      return expression == Expression.NORMAL;
    default:
      return false;
    }
  }

  private boolean peekEqualityOperator() {
    switch (peekKind()) {
    case JS_EQUAL_EQUAL:
    case JS_NOT_EQUAL:
    case JS_EQUAL_EQUAL_EQUAL:
    case JS_NOT_EQUAL_EQUAL:
      return true;
    default:
      return false;
    }
  }

  private boolean peekAssignmentOperator() {
    switch (peekKind()) {
    case JS_EQUAL:
    case JS_AMPERSAND_EQUAL:
    case JS_BAR_EQUAL:
    case JS_STAR_EQUAL:
    case JS_SLASH_EQUAL:
    case JS_PERCENT_EQUAL:
    case JS_PLUS_EQUAL:
    case JS_MINUS_EQUAL:
    case JS_LEFT_SHIFT_EQUAL:
    case JS_RIGHT_SHIFT_EQUAL:
    case JS_UNSIGNED_RIGHT_SHIFT_EQUAL:
    case JS_HAT_EQUAL:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parsePrimaryExpression() {
    switch (peekKind()) {
    case JS_THIS:
      return parseThisExpression();
    case JS_OPEN_SQUARE:
      return parseArrayLiteral();
    case JS_OPEN_CURLY:
      return parseObjectLiteral();
    case JS_OPEN_PAREN:
      return parseParenExpression();
    case JS_IDENTIFIER:
      return parseIdentifier();
    case JS_NULL:
    case JS_TRUE:
    case JS_FALSE:
    case JS_NUMBER:
    case JS_STRING:
      // TODO: Regular Expression literals go here.
      return parseLiteral();
    default:
      reportError(nextToken(), "Expected expression.");
      return null;
    }
  }

  private ParseTree parseObjectLiteral() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> elements = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.JS_OPEN_CURLY);
    while (peekPropertyAssignment()) {
      elements.add(parsePropertyAssignment());
      if (!eatOpt(TokenKind.JS_COMMA)) {
        break;
      }
    }
    eat(TokenKind.JS_CLOSE_CURLY);
    return new ObjectLiteralExpressionTree(getRange(start), elements.build());
  }

  private ParseTree parsePropertyAssignment() {
    if (peekGetProperty()) {
      return parseGetProperty();
    } else if (peekSetProperty()) {
      return parseSetProperty();
    } else {
      return parseProperty();
    }
  }

  private ParseTree parseProperty() {
    Token start = peek();
    Token property = nextToken();
    eat(TokenKind.JS_COLON);
    return new PropertyAssignmentTree(getRange(start), property, parseAssignmentExpression(Expression.NORMAL));
  }

  private ParseTree parseSetProperty() {
    Token start = peek();
    eatId(); // set
    Token property = nextToken();
    eat(TokenKind.JS_COLON);
    eat(TokenKind.JS_OPEN_PAREN);
    IdentifierToken parameter = eatId();
    eat(TokenKind.JS_CLOSE_PAREN);
    BlockTree body = parseBlock();
    return new SetAccessorTree(getRange(start), property, parameter, body);
  }

  private ParseTree parseGetProperty() {
    Token start = peek();
    eatId(); // get
    Token property = nextToken();
    eat(TokenKind.JS_COLON);
    eat(TokenKind.JS_OPEN_PAREN);
    eat(TokenKind.JS_CLOSE_PAREN);
    BlockTree body = parseBlock();
    return new GetAccessorTree(getRange(start), property, body);
  }

  private boolean peekSetProperty() {
    return peekPredefinedName(JavascriptPredefinedNames.SET) && peekPropertyAssignment(1);
  }

  private boolean peekGetProperty() {
    return peekPredefinedName(JavascriptPredefinedNames.GET) && peekPropertyAssignment(1);
  }

  private boolean peekPredefinedName(String name) {
    IdentifierToken identifier = peekIdentifier();
    return identifier != null && identifier.value.equals(name);
  }

  private IdentifierToken peekIdentifier() {
    Token token = peek();
    return token.isJavascriptIdentifier() ? token.asJavascriptIdentifier() : null;
  }

  private boolean peekPropertyAssignment() {
    return peekPropertyAssignment(0);
  }

  private boolean peekPropertyAssignment(int index) {
    switch (peekKind(index)) {
    case JS_IDENTIFIER:
    case JS_STRING:
    case JS_NUMBER:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseArrayLiteral() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> elements = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.JS_OPEN_SQUARE);
    while (peek(TokenKind.JS_COMMA) || peekAssignmentExpression()) {
      if (peek(TokenKind.JS_COMMA)) {
        elements.add(new ElisionTree(getRange(eat(TokenKind.JS_COMMA))));
      } else {
        elements.add(parseAssignmentExpression(Expression.NORMAL));
      }
    }
    eat(TokenKind.JS_CLOSE_SQUARE);
    return new ArrayLiteralExpressionTree(getRange(start), elements.build());
  }

  private boolean peekAssignmentExpression() {
    return peekExpression();
  }

  private ParseTree parseThisExpression() {
    Token start = eat(TokenKind.JS_THIS);
    return new ThisExpressionTree(getRange(start));
  }

  private ParseTree parseParenExpression() {
    Token start = peek();
    eat(TokenKind.JS_OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.JS_CLOSE_PAREN);
    return new ParenExpressionTree(getRange(start), expression);
  }

  private ParseTree parseIdentifier() {
    IdentifierToken name = eatId();
    return new IdentifierExpressionTree(getRange(name), name);
  }

  private ParseTree parsePostfixExpression() {
    Token start = peek();
    ParseTree left = parseLeftHandSideExpression();
    while (peekPostfixOperator()) {
      left = new PostfixExpressionTree(getRange(start), left, nextToken());
    }
    return left;
  }

  private ParseTree parseLeftHandSideExpression() {
    Token start = peek();
    ParseTree operand = parseNewExpression();
    while (peekCallSuffix()) {
      switch (peekKind()) {
      case JS_OPEN_PAREN:
        operand = parseCallSuffix(start, operand);
        break;
      case JS_OPEN_SQUARE:
        operand = parseArraySuffix(start, operand);
        break;
      case JS_PERIOD:
        operand = parseMemberAccessSuffix(start, operand);
        break;
      default:
        throw new RuntimeException("Unexpected call suffix.");
      }
    }
    return operand;
  }

  private ParseTree parseMemberAccessSuffix(Token start, ParseTree operand) {
    return new MemberExpressionTree(getRange(start), operand, eatId());
  }

  private ParseTree parseCallSuffix(Token start, ParseTree operand) {
    return new CallExpressionTree(getRange(start), operand, parseArguments());
  }

  private ParseTree parseArraySuffix(Token start, ParseTree operand) {
    return new ArrayAccessExpressionTree(getRange(start), operand, parseArrayIndex());
  }

  private boolean peekCallSuffix() {
    switch (peekKind()) {
    case JS_OPEN_PAREN:
    case JS_OPEN_SQUARE:
    case JS_PERIOD:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseNewExpression() {
    Token start = peek();
    if (eatOpt(TokenKind.JS_NEW)) {
      ParseTree operand = parseNewExpression();
      ArgumentsTree arguments = null;
      if (peekArguments()) {
        arguments = parseArguments();
      }
      return new NewExpressionTree(getRange(start), operand, arguments);
    } else {
      return parseMemberExpressionNoNew();
    }
  }

  private ParseTree parseMemberExpressionNoNew() {
    Token start = peek();
    ParseTree operand;
    if (peek(TokenKind.JS_FUNCTION)) {
      operand = parseFunction();
    } else {
      operand = parsePrimaryExpression();
    }
    while (peekMemberExpressionSuffix()) {
      switch (peekKind()) {
      case JS_PERIOD:
        operand = parseMemberAccessSuffix(start, operand);
        break;
      case JS_OPEN_SQUARE:
        operand = parseArraySuffix(start, operand);
        break;
      default:
        throw new RuntimeException("Unexpected member expression suffix.");
      }
    }
    return operand;
  }

  private ParseTree parseArrayIndex() {
    eat(TokenKind.JS_OPEN_SQUARE);
    ParseTree index = parseExpression();
    eat(TokenKind.JS_CLOSE_SQUARE);
    return index;
  }

  private boolean peekMemberExpressionSuffix() {
    switch (peekKind()) {
    case JS_PERIOD:
    case JS_OPEN_SQUARE:
      return true;
    default:
      return false;
    }
  }

  private boolean peekArguments() {
    return peek(TokenKind.JS_OPEN_PAREN);
  }

  private ArgumentsTree parseArguments() {
    Token start = peek();
    eat(TokenKind.JS_OPEN_PAREN);
    ImmutableList.Builder<ParseTree> arguments = new ImmutableList.Builder<ParseTree>();
    if (peekExpression()) {
      do {
        arguments.add(parseAssignmentExpression(Expression.NORMAL));
      } while (eatOpt(TokenKind.JS_COMMA));
    }
    eat(TokenKind.JS_CLOSE_PAREN);
    return new ArgumentsTree(getRange(start), arguments.build());
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
    switch (peekKind()) {
    // expression
    case JS_OPEN_PAREN:
    case JS_OPEN_SQUARE:
    case JS_NULL:
    case JS_THIS:
    case JS_TRUE:
    case JS_FALSE:
    case JS_IDENTIFIER:
    case JS_NUMBER:
    case JS_STRING:
    case JS_NEW:
    case JS_DELETE:
    case JS_TYPEOF:
    case JS_VOID:
    case JS_PLUS_PLUS:
    case JS_MINUS_MINUS:
    case JS_PLUS:
    case JS_MINUS:
    case JS_BANG:
    case JS_TILDE:

    // expression or statement
    case JS_FUNCTION:
    case JS_OPEN_CURLY:

    // statements
    case JS_VAR:
    case JS_SEMI_COLON:
    case JS_IF:
    case JS_DO:
    case JS_WHILE:
    case JS_FOR:
    case JS_CONTINUE:
    case JS_BREAK:
    case JS_RETURN:
    case JS_WITH:
    case JS_SWITCH:
    case JS_THROW:
    case JS_TRY:
    case JS_DEBUGGER:
      return true;

    }
    return peekExpression();
  }

  private boolean peekExpression() {
    switch (peekKind()) {
    case JS_FUNCTION:
    case JS_OPEN_CURLY:
    case JS_OPEN_PAREN:
    case JS_OPEN_SQUARE:
    case JS_NULL:
    case JS_THIS:
    case JS_TRUE:
    case JS_FALSE:
    case JS_IDENTIFIER:
    case JS_NUMBER:
    case JS_STRING:
    case JS_NEW:
    case JS_DELETE:
    case JS_TYPEOF:
    case JS_VOID:
    case JS_PLUS_PLUS:
    case JS_MINUS_MINUS:
    case JS_PLUS:
    case JS_MINUS:
    case JS_BANG:
    case JS_TILDE:
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
