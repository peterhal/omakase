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
 * Parser for the Omakase language.
 *
 * There are 3 kinds of methods - parsing, peeking and scanning.
 *
 * parse* methods, parse a given construct returning the parsed tree. The position is advanced to the end of the parsed
 *    construct. Parsing methods must report any parse errors encountered.
 *
 * peek* methods do simple lookahead for simple disambiguation. The return value is a boolean indicating the presence of
 *    the peeked construct. The position in the token stream is unchanged. Peek methods must never report errors.
 *
 * scan* methods do complex disambiguation. They return enums indicating one of several alternatives, or an indication
 *    that the lookahead was ambiguous and more lookahead will be needed to resolve the ambiguity. Scan methods leave
 *    the token stream at the end of the scanned construct. Scanning must never report errors.
 */
public class Parser extends ParserBase {
  public Parser(ErrorReporter reporter, SourceFile file) {
    super(reporter, file, new Scanner(reporter, file));
  }

  public static SourceFileTree parse(ErrorReporter reporter, SourceFile file) {
    return new Parser(reporter, file).parseFile();
  }

  private SourceFileTree parseFile() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> declarations = new ImmutableList.Builder<ParseTree>();
    while (peekClass()) {
      declarations.add(parseClass());
    }
    eat(TokenKind.END_OF_FILE);

    return new SourceFileTree(getRange(start), declarations.build());
  }

  // Parse Class Members
  private FormalParameterListTree parseParameterListDeclaration(boolean typesRequired) {
    Token start = peek();
    ImmutableList.Builder<ParameterDeclarationTree> result = new ImmutableList.Builder<ParameterDeclarationTree>();
    eat(TokenKind.OPEN_PAREN);
    if (peekParameter()) {
      result.add(parseParameter());
      while (eatOpt(TokenKind.COMMA)) {
        result.add(parseParameter());
      }
    }
    eat(TokenKind.CLOSE_PAREN);
    ImmutableList<ParameterDeclarationTree> parameters = result.build();
    if (!parameters.isEmpty()) {
      // Types are either all specified or none.
      if (typesRequired || parameters.get(0).asParameterDeclaration().type != null) {
        for (ParameterDeclarationTree parameter : parameters) {
          if (parameter.type == null) {
            reportError(parameter.name, "Parameter type required");
          }
        }
      } else {
        for (ParameterDeclarationTree parameter : parameters) {
          if (parameter.type != null) {
            reportError(parameter.type.start(), "Parameter type unexpected.");
          }
        }
      }
    }

    return new FormalParameterListTree(getRange(start), parameters);
  }

  private ParameterDeclarationTree parseParameter() {
    Token start = peek();
    IdentifierToken name = eatId();
    ParseTree type = parseColonTypeOpt();
    return new ParameterDeclarationTree(getRange(start), type, name);
  }

  private ParseTree parseColonTypeOpt() {
    ParseTree type = null;
    if (eatOpt(TokenKind.COLON)) {
      type = parseType();
    }
    return type;
  }

  private boolean peekParameter() {
    return peekParameter(0);
  }

  private boolean peekParameter(int index) {
    return peek(index, TokenKind.IDENTIFIER);
  }

  private boolean peekClassMember() {
    switch (peekKind()) {
    case NATIVE:
    case STATIC:
    case VAR:
    case IDENTIFIER:
      return true;
    }
    return false;
  }

  private boolean peekClass() {
    return peek(TokenKind.CLASS) || peek(TokenKind.EXTERN);
  }

  private ParseTree parseClass() {
    Token start = peek();
    boolean isExtern = eatOpt(TokenKind.EXTERN);
    eat(TokenKind.CLASS);
    IdentifierToken name = eatId();
    eat(TokenKind.OPEN_CURLY);
    ImmutableList<ParseTree> members = parseClassMembers(isExtern);
    eat(TokenKind.CLOSE_CURLY);
    return new ClassDeclarationTree(getRange(start), isExtern, name, members);
  }

  private ImmutableList<ParseTree> parseClassMembers(boolean isExtern) {
    ImmutableList.Builder<ParseTree> members = new ImmutableList.Builder<ParseTree>();
    while (peekClassMember()) {
      members.add(parseClassMember(isExtern));
    }
    return members.build();
  }

  private ParseTree parseClassMember(boolean isExtern) {
    Token start = peek();
    boolean isStatic = eatOpt(TokenKind.STATIC);
    boolean isNative = eatOpt(TokenKind.NATIVE);
    if (peek(TokenKind.VAR)) {
      if (isNative) {
        reportError(start, "Fields may not be native.");
      }
      return parseField(start, isExtern, isStatic);
    }
    return parseMethod(start, isExtern, isNative, isStatic);
  }

  private ParseTree parseField(Token start, boolean isExtern, boolean isStatic) {
    eat(TokenKind.VAR);
    ImmutableList<VariableDeclarationTree> declarations = parseVariableDeclarations();
    if (isExtern) {
      for (VariableDeclarationTree declaration : declarations) {
        if (declaration.initializer != null) {
          reportError(declaration.initializer.start(), "Extern fields may not have initializers.");
        }
      }
    }
    eat(TokenKind.SEMI_COLON);
    return new FieldDeclarationTree(getRange(start), isStatic, declarations);
  }

  private ParseTree parseMethod(Token start, boolean isExtern, boolean isNative, boolean isStatic) {
    IdentifierToken name = eatId();
    FormalParameterListTree formals = parseParameterListDeclaration(true);
    ParseTree returnType = parseColonType();
    ParseTree body;
    if (isExtern) {
      body = null;
      eat(TokenKind.SEMI_COLON);
    } else {
      body = parseBlock(isNative);
    }
    return new MethodDeclarationTree(getRange(start), returnType, name, formals, isStatic, isNative, body);
  }

  private ParseTree parseColonType() {
    eat(TokenKind.COLON);
    return parseType();
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

  // Types
  private ParseTree parseType() {
    Token start = peek();
    ParseTree elementType = parseElementType();
    do {
      switch (peekKind()) {
      case OPEN_SQUARE:
        eat(TokenKind.OPEN_SQUARE);
        eat(TokenKind.CLOSE_SQUARE);
        elementType = new ArrayTypeTree(getRange(start), elementType);
        break;
      case QUESTION:
        eat(TokenKind.QUESTION);
        elementType = new NullableTypeTree(getRange(start), elementType);
        break;
      default:
        return elementType;
      }
    } while (true);
  }

  private ParseTree parseElementType() {
    switch (peekKind()) {
    case OPEN_PAREN:
      return parseFunctionType();
    case IDENTIFIER:
      return parseNamedType();
    case BOOL:
    case DYNAMIC:
    case NUMBER:
    case OBJECT:
    case STRING:
    case VOID:
      return parseKeywordType();
    default:
      reportError(peek(), "Type expected");
      return null;
    }
  }

  private ParseTree parseNamedType() {
    Token start = peek();
    NamedTypeTree element = parseNamedTypeElement(start, null);
    while (eatOpt(TokenKind.PERIOD)) {
      element = parseNamedTypeElement(start, element);
    }
    return element;
  }

  private NamedTypeTree parseNamedTypeElement(Token start, NamedTypeTree element) {
    IdentifierToken name = eatId();
    TypeArgumentListTree typeArguments = parseTypeArgumentsOpt();
    return new NamedTypeTree(getRange(start), element, name, typeArguments);
  }

  private TypeArgumentListTree parseTypeArgumentsOpt() {
    if (peek(TokenKind.OPEN_ANGLE)) {
      return parseTypeArguments();
    }
    return null;
  }

  private TypeArgumentListTree parseTypeArguments() {
    Token start = peek();
    eat(TokenKind.OPEN_ANGLE);
    ImmutableList.Builder<ParseTree> typeArguments = new ImmutableList.Builder<ParseTree>();
    typeArguments.add(parseType());
    while (eatOpt(TokenKind.COMMA)) {
      typeArguments.add(parseType());
    }
    eat(TokenKind.CLOSE_ANGLE);
    return new TypeArgumentListTree(getRange(start), typeArguments.build());
  }

  private boolean peekType() {
    switch (peekKind()) {
    case BOOL:
    case DYNAMIC:
    case IDENTIFIER:
    case NUMBER:
    case OBJECT:
    case OPEN_PAREN:
    case STRING:
    case VOID:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseKeywordType() {
    Token start = peek();
    nextToken();
    return new KeywordTypeTree(getRange(start), start);
  }

  private ParseTree parseFunctionType() {
    Token start = peek();
    eat(TokenKind.OPEN_PAREN);
    ImmutableList.Builder<ParseTree> parameterTypes = new ImmutableList.Builder<ParseTree>();
    if (peekType()) {
      parameterTypes.add(parseType());
      while(eatOpt(TokenKind.COMMA)) {
        parameterTypes.add(parseType());
      }
    }
    eat(TokenKind.CLOSE_PAREN);
    eat(TokenKind.ARROW);
    ParseTree returnType = parseType();
    return new FunctionTypeTree(getRange(start), parameterTypes.build(), returnType);
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
    case IDENTIFIER:
    case VOID:
    case STRING:
    case OBJECT:
    case BOOL:
    case DYNAMIC:
    case NUMBER:
    case OPEN_SQUARE:
    case NULL:
    case THIS:
    case TRUE:
    case FALSE:
    case NUMBER_LITERAL:
    case STRING_LITERAL:
    case NEW:
    case TYPEOF:
    case PLUS_PLUS:
    case MINUS_MINUS:
    case PLUS:
    case MINUS:
    case BANG:
    case TILDE:
      return parseExpressionStatement();

    // statements
    case OPEN_CURLY:
      return parseBlock();
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
    ImmutableList<VariableDeclarationTree> declarations = parseVariableDeclarations();
    eat(TokenKind.SEMI_COLON);
    return new VariableStatementTree(getRange(start), declarations);
  }

  private ImmutableList<VariableDeclarationTree> parseVariableDeclarations() {
    return parseRemainingVariableDeclarations(parseVariableDeclaration());
  }

  private ImmutableList<VariableDeclarationTree> parseRemainingVariableDeclarations(VariableDeclarationTree element) {
    ImmutableList.Builder<VariableDeclarationTree> declarations = new ImmutableList.Builder<VariableDeclarationTree>();
    declarations.add(element);
    while (eatOpt(TokenKind.COMMA)) {
      declarations.add(parseVariableDeclaration());
    }
    return declarations.build();
  }

  private VariableDeclarationTree parseVariableDeclaration() {
    Token start = peek();
    IdentifierToken identifier = eatId();
    return parseVariableDeclaration(start, identifier);
  }

  private VariableDeclarationTree parseVariableDeclaration(Token start, IdentifierToken identifier) {
    ParseTree type = parseColonTypeOpt();
    ParseTree initializer = null;
    if (eatOpt(TokenKind.EQUAL)) {
      initializer = parseExpression();
    }
    return new VariableDeclarationTree(getRange(start), identifier, type, initializer);
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
      VariableDeclarationTree variableDeclaration = parseVariableDeclaration();
      if (eatOpt(TokenKind.IN)) {
        if (variableDeclaration.initializer != null) {
          reportError(variableDeclaration.initializer.start(), "Variable declared in for-in statement may not have an initializer.");
        }
        return parseForIn(start, variableDeclaration);
      } else {
        return parseForStatement(start,
            new VariableStatementTree(getRange(variableStart), parseRemainingVariableDeclarations(variableDeclaration)));
      }
    case SEMI_COLON:
      return parseForStatement(start, null);
    default:
      ParseTree initializer = parseExpression();
      return parseForStatement(start, initializer);
    }
  }

  private ParseTree parseForStatement(Token start, ParseTree initializer) {
    eat(TokenKind.SEMI_COLON);
    ParseTree condition = peek(TokenKind.SEMI_COLON) ? null : parseExpression();
    eat(TokenKind.SEMI_COLON);
    ParseTree increment = peek(TokenKind.CLOSE_PAREN) ? null : parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new ForStatementTree(getRange(start), initializer, condition, increment, parseStatement());
  }

  private ParseTree parseForIn(Token start, VariableDeclarationTree variableDeclaration) {
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
    ParseTree expression = null;
    if (peekExpression()) {
      expression = parseExpression();
    }
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
    ParseTree defaultClause = null;
    while (peekCaseClause()) {
      ParseTree clause = parseCaseClause();
      if (clause.isDefaultClause()) {
        if (defaultClause == null) {
          defaultClause = clause;
        } else {
          reportError(clause.start(), "Duplicate default clause in switch.");
        }
      }
      caseClauses.add(clause);
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
    eat(TokenKind.CASE);
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
    case NUMBER_LITERAL:
    case STRING_LITERAL:
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

  // Expressions

  // expression:
  //    function-expression
  //    conditional-expression
  //    assignment-expression
  private ParseTree parseExpression() {
    if (peekFunction()) {
      return parseFunction();
    }
    ParseTree left = parseConditionalExpression();
    if (!peekAssignmentOperator()) {
      return left;
    }
    return parseAssignmentExpression(left);
  }

  private ParseTree parseAssignmentExpression(ParseTree left) {
    Token operator = nextToken();
    checkForLeftHandSideExpression(left, operator);
    ParseTree right = parseExpression();
    return new BinaryExpressionTree(getRange(left.start()), left, operator, right);
  }

  private void checkForLeftHandSideExpression(ParseTree left, Token operator) {
    switch (left.kind) {
    case ARRAY_ACCESS_EXPRESSION:
    case CALL_EXPRESSION:
    case PAREN_EXPRESSION:
    case IDENTIFIER_EXPRESSION:
    case MEMBER_EXPRESSION:
    case NEW_EXPRESSION:
    case POSTFIX_EXPRESSION:
    case THIS_EXPRESSION:
    case UNARY_EXPRESSION:
      break;
    case ARRAY_LITERAL_EXPRESSION:
    case BINARY_EXPRESSION:
    case CONDITIONAL_EXPRESSION:
    case FUNCTION_EXPRESSION:
    case LITERAL_EXPRESSION:
      reportError(operator, "Left hand side of an assignment operator may not be conditional, function, literal or binary expressions.");
      break;
    default:
      throw new RuntimeException("Unexpected expression kind");
    }
  }

  private ParseTree parseFunction() {
    Token start = peek();
    FormalParameterListTree parameters = parseParameterListDeclaration(false);
    eat(TokenKind.ARROW);
    ParseTree body;
    if (peek(TokenKind.OPEN_CURLY)) {
      body = parseBlock();
    } else {
      body = parseExpression();
    }
    return new FunctionExpressionTree(getRange(start), parameters, body);
  }

  private boolean peekFunction() {
    if (!peek(TokenKind.OPEN_PAREN)) {
      return false;
    }
    int index = 1;
    if (peekParameter(index)) {
      index++;
      while (peek(index, TokenKind.COMMA)) {
        index ++;
        if (peekParameter(index)) {
          index++;
        } else {
          return false;
        }
      }
    }
    if (!peek(index, TokenKind.CLOSE_PAREN)) {
      return false;
    }
    return peek(index + 1, TokenKind.ARROW);
  }

  private ParseTree parseConditionalExpression() {
    Token start = peek();
    ParseTree condition = parseLogicalOrExpression();
    if (!eatOpt(TokenKind.QUESTION)) {
      return condition;
    }
    ParseTree trueCase = parseExpression();
    eat(TokenKind.COLON);
    ParseTree falseCase = parseExpression();
    return new ConditionalExpressionTree(getRange(start), condition, trueCase, falseCase);
  }

  private ParseTree parseLogicalOrExpression() {
    Token start = peek();
    ParseTree left = parseLogicalAndExpression();
    while (peek(TokenKind.BAR_BAR)) {
      Token operator = nextToken();
      ParseTree right = parseLogicalAndExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseLogicalAndExpression() {
    Token start = peek();
    ParseTree left = parseBitwiseOrExpression();
    while (peek(TokenKind.AMPERSAND_AMPERSAND)) {
      Token operator = nextToken();
      ParseTree right = parseBitwiseOrExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseOrExpression() {
    Token start = peek();
    ParseTree left = parseBitwiseXorExpression();
    while (peek(TokenKind.BAR)) {
      Token operator = nextToken();
      ParseTree right = parseBitwiseXorExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseXorExpression() {
    Token start = peek();
    ParseTree left = parseBitwiseAndExpression();
    while (peek(TokenKind.HAT)) {
      Token operator = nextToken();
      ParseTree right = parseBitwiseAndExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseAndExpression() {
    Token start = peek();
    ParseTree left = parseEqualityExpression();
    while (peek(TokenKind.AMPERSAND)) {
      Token operator = nextToken();
      ParseTree right = parseEqualityExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseEqualityExpression() {
    Token start = peek();
    ParseTree left = parseRelationalExpression();
    while (peekEqualityOperator()) {
      Token operator = nextToken();
      ParseTree right = parseRelationalExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseRelationalExpression() {
    Token start = peek();
    ParseTree left = parseInstanceOfExpression();
    while (peekRelationalOperator()) {
      Token operator = nextToken();
      ParseTree right = parseInstanceOfExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseInstanceOfExpression() {
    Token start = peek();
    ParseTree left = parseShiftExpression();
    while (peek(TokenKind.INSTANCEOF)) {
      Token operator = nextToken();
      ParseTree right = parseType();
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
    case TYPEOF:
    case PLUS_PLUS:
    case MINUS_MINUS:
    case PLUS:
    case MINUS:
    case TILDE:
    case BANG:
      return true;
    default:
      return false;
    }
  }

  private boolean peekMultiplicativeOperator() {
    switch (peekKind()) {
    case STAR:
    case SLASH:
    case PERCENT:
      return true;
    default:
      return false;
    }
  }

  private boolean peekAdditiveOperator() {
    switch (peekKind()) {
    case PLUS:
    case MINUS:
      return true;
    default:
      return false;
    }
  }

  private boolean peekShiftOperator() {
    switch (peekKind()) {
    case SHIFT_LEFT:
    case SHIFT_RIGHT:
      return true;
    default:
      return false;
    }
  }

  private boolean peekRelationalOperator() {
    switch (peekKind()) {
    case OPEN_ANGLE:
    case CLOSE_ANGLE:
    case GREATER_EQUAL:
    case LESS_EQUAL:
      return true;
    default:
      return false;
    }
  }

  private boolean peekEqualityOperator() {
    switch (peekKind()) {
    case EQUAL_EQUAL:
    case NOT_EQUAL:
      return true;
    default:
      return false;
    }
  }

  private boolean peekAssignmentOperator() {
    switch (peekKind()) {
    case EQUAL:
    case AMPERSAND_EQUAL:
    case BAR_EQUAL:
    case STAR_EQUAL:
    case SLASH_EQUAL:
    case PERCENT_EQUAL:
    case PLUS_EQUAL:
    case MINUS_EQUAL:
    case LEFT_SHIFT_EQUAL:
    case RIGHT_SHIFT_EQUAL:
    case HAT_EQUAL:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parsePrimaryExpression() {
    switch (peekKind()) {
    case THIS:
      return parseThisExpression();
    case OPEN_SQUARE:
      return parseArrayLiteral();
    case OPEN_PAREN:
      return parseParenExpression();
    case IDENTIFIER:
      // TODO: Type ArgumentList opt
      // Note: Must disambiguate with relational expression.
      return parseIdentifier();
    case NULL:
    case TRUE:
    case FALSE:
    case NUMBER_LITERAL:
    case STRING_LITERAL:
      // TODO: Regular Expression literals go here.
      return parseLiteral();
    default:
      reportError(nextToken(), "Expected expression.");
      return null;
    }
  }

  private ParseTree parseArrayLiteral() {
    Token start = peek();
    ImmutableList.Builder<ParseTree> elements = new ImmutableList.Builder<ParseTree>();
    eat(TokenKind.OPEN_SQUARE);
    if (peekExpression()) {
      elements.add(parseExpression());
      while (eatOpt(TokenKind.COMMA)) {
        elements.add(parseExpression());
      }
    }
    eat(TokenKind.CLOSE_SQUARE);
    return new ArrayLiteralExpressionTree(getRange(start), elements.build());
  }

  private ParseTree parseThisExpression() {
    Token start = eat(TokenKind.THIS);
    return new ThisExpressionTree(getRange(start));
  }

  private ParseTree parseParenExpression() {
    Token start = peek();
    eat(TokenKind.OPEN_PAREN);
    ParseTree expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
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
      case OPEN_PAREN:
        operand = parseCallSuffix(start, operand);
        break;
      case OPEN_SQUARE:
        operand = parseArraySuffix(start, operand);
        break;
      case PERIOD:
        operand = parseMemberAccessSuffix(start, operand);
        break;
      default:
        throw new RuntimeException("Unexpected call suffix.");
      }
    }
    return operand;
  }

  private ParseTree parseMemberAccessSuffix(Token start, ParseTree operand) {
    eat(TokenKind.PERIOD);
    // TODO: Add Type argument list opt
    // Note: Must disambiguate with relational expression.
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
    case OPEN_PAREN:
    case OPEN_SQUARE:
    case PERIOD:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseNewExpression() {
    Token start = peek();
    if (eatOpt(TokenKind.NEW)) {
      // TODO: Should be parseTypeName.
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
    operand = parsePrimaryExpression();
    while (peekMemberExpressionSuffix()) {
      switch (peekKind()) {
      case PERIOD:
        operand = parseMemberAccessSuffix(start, operand);
        break;
      case OPEN_SQUARE:
        operand = parseArraySuffix(start, operand);
        break;
      default:
        throw new RuntimeException("Unexpected member expression suffix.");
      }
    }
    return operand;
  }

  private ParseTree parseArrayIndex() {
    eat(TokenKind.OPEN_SQUARE);
    ParseTree index = parseExpression();
    eat(TokenKind.CLOSE_SQUARE);
    return index;
  }

  private boolean peekMemberExpressionSuffix() {
    switch (peekKind()) {
    case PERIOD:
    case OPEN_SQUARE:
      return true;
    default:
      return false;
    }
  }

  private boolean peekPostfixOperator() {
    switch (peekKind()) {
    case PLUS_PLUS:
    case MINUS_MINUS:
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
    case OPEN_CURLY:
    case OPEN_PAREN:
    case OPEN_SQUARE:
    case NULL:
    case THIS:
    case TRUE:
    case FALSE:
    case IDENTIFIER:
    case NUMBER_LITERAL:
    case STRING_LITERAL:
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
    default:
      return false;
    }
  }

  private boolean peekArguments() {
    return peek(TokenKind.OPEN_PAREN);
  }

  private ArgumentsTree parseArguments() {
    Token start = peek();
    eat(TokenKind.OPEN_PAREN);
    ImmutableList.Builder<ParseTree> arguments = new ImmutableList.Builder<ParseTree>();
    if (peekExpression()) {
      do {
        arguments.add(parseExpression());
      } while (eatOpt(TokenKind.COMMA));
    }
    eat(TokenKind.CLOSE_PAREN);
    return new ArgumentsTree(getRange(start), arguments.build());
  }

  private IdentifierToken eatId() {
    Token token = eat(TokenKind.IDENTIFIER);

    return token.isIdentifier() ? token.asIdentifier() : null;
  }

  private IdentifierToken eatIdOpt() {
    if (peek(TokenKind.IDENTIFIER)) {
      return eatId();
    }
    return null;
  }

}
