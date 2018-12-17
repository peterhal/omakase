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
import com.sun.source.tree.TypeParameterTree;
import omakase.syntax.tokens.IdentifierToken;
import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.trees.*;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;
import omakase.util.SourceRange;

import static omakase.syntax.PredefinedNames.CONSTRUCTOR;
import static omakase.syntax.PredefinedNames.JAVASCRIPT;
import static omakase.syntax.tokens.TokenKind.CLOSE_ANGLE;
import static omakase.syntax.tokens.TokenKind.NATIVE;
import static omakase.syntax.tokens.TokenKind.OPEN_ANGLE;

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
    var start = peek();
    var declarations = parseList(this::peekDeclaration, this::parseDeclaration);
    eat(TokenKind.END_OF_FILE);
    return new SourceFileTree(getRange(start), declarations);
  }

  private boolean peekDeclaration() {
    switch (peekKind()) {
    // TODO: global vars/constants
    case EXTERN:
    case NATIVE:
    case CLASS:
    case FUNCTION:
      return true;
    case IDENTIFIER:
      return peekModifier();
    default:
      return false;
    }
  }

  private boolean peekModifier() {
    switch (peekKind()) {
    // TODO: private/protected/public
    case EXTERN:
    case NATIVE:
    case STATIC:
      return true;
    case IDENTIFIER:
      return isModifier(peek().asIdentifier().value);
    default:
      return false;
    }
  }

  private static boolean isModifier(String value) {
    switch (value) {
    case JAVASCRIPT:
      return true;
    default:
      return false;
    }
  }

  public interface ValidateModifier {
    boolean isValid(Token token);
  }

  private ImmutableList<Token> parseModifiers() {
    var elements = new ImmutableList.Builder<Token>();
    while (peekModifier()) {
      elements.add(nextToken());
    }

    var result = elements.build();
    for (int index = 0; index < result.size(); index++) {
      var token = result.get(index);
      for (int i = 0; i < index; i++) {
        var other = result.get(i);
        if (token.kind.equals(other.kind) && (!token.isIdentifier() || (token.valueString().equals(other.valueString())))) {
          reportError(token, "Duplicate modifier");
        }
      }
    }
    return result;
  }

  private void validateModifiers(ImmutableList<Token> modifiers, ValidateModifier isValid) {
    for (var modifier: modifiers) {
      if (!isValid.isValid(modifier)) {
        reportError(modifier, "Invalid modifier");
      }
    }
  }

  private ParseTree parseDeclaration() {
    var start = peek();
    var modifiers = parseModifiers();
    switch (peekKind()) {
    case FUNCTION:
      return parseFunctionDeclaration(start, modifiers);
    default:
      return parseClass(start, modifiers);
    }
  }

  private static boolean isValidFunctionModifier(Token token) {
    switch (token.kind) {
    case EXTERN:
    case NATIVE:
      return true;
    case IDENTIFIER:
      return token.asIdentifier().value.equals(JAVASCRIPT);
    default:
      return false;
    }
  }

  private static boolean hasModifier(ImmutableList<Token> modifiers, TokenKind kind) {
    return modifiers.stream().anyMatch(modifier -> modifier.kind.equals(kind));
  }

  private static boolean hasModifier(ImmutableList<Token> modifiers, String name) {
    return modifiers.stream().anyMatch(modifier -> modifier.kind.equals(TokenKind.IDENTIFIER) && modifier.asIdentifier().value.equals(name));
  }

  private ParseTree parseFunctionDeclaration(Token start, ImmutableList<Token> modifiers) {
    validateModifiers(modifiers, Parser::isValidFunctionModifier);
    var isExtern = hasModifier(modifiers, TokenKind.EXTERN);
    var isNative = hasModifier(modifiers, NATIVE);
    var isJavascript = hasModifier(modifiers, JAVASCRIPT);
    if (isJavascript && isNative) {
      reportError(peek(), "Declaration may not be both 'native' and 'javascript'.");
    }
    eat(TokenKind.FUNCTION);
    var name = eatId();
    var parameters = parseParameterListDeclaration(true);
    var returnType = parseColonType();
    ParseTree body;
    if (isNative) {
      body = null;
      eat(TokenKind.SEMI_COLON);
    } else {
      body = parseBlock(isJavascript);
    }
    return new FunctionDeclarationTree(getRange(start), returnType, name, parameters, isExtern, isNative, isJavascript, body);
  }

  // Parse Class Members
  private FormalParameterListTree parseParameterListDeclaration(boolean typesRequired) {
    var start = peek();
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
    var start = peek();
    IdentifierToken name = eatId();
    var type = parseColonTypeOpt();
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

  private static boolean isValidClassModifier(Token token) {
    switch (token.kind) {
    case EXTERN:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseClass(Token start, ImmutableList<Token> modifiers) {
    validateModifiers(modifiers, Parser::isValidClassModifier);
    var isExtern = hasModifier(modifiers, TokenKind.EXTERN);
    eat(TokenKind.CLASS);
    IdentifierToken name = eatId();
    var typeParameters = parseTypeParametersOpt();
    eat(TokenKind.OPEN_CURLY);
    var members = parseClassMembers(isExtern);
    eat(TokenKind.CLOSE_CURLY);
    return new ClassDeclarationTree(getRange(start), isExtern, name, typeParameters, members);
  }

  private ImmutableList<ParseTree> parseTypeParametersOpt() {
    if (eatOpt(OPEN_ANGLE)) {
      var result = parseCommaSeparatedList(this::parseTypeParameter);
      eat(CLOSE_ANGLE);
      return result;
    } else {
      return ImmutableList.of();
    }
  }

  private TypeParameterDeclarationTree parseTypeParameter() {
    var start = peek();
    IdentifierToken name = eatId();
    var bounds = parseColonTypeOpt();
    return new TypeParameterDeclarationTree(getRange(start), bounds, name);
  }

  private ImmutableList<ParseTree> parseClassMembers(final boolean isExtern) {
    return this.parseList(
        this::peekClassMember,
        () -> this.parseClassMember(isExtern));
  }

  private ParseTree parseClassMember(boolean isExtern) {
    var start = peek();
    var modifiers = parseModifiers();
    if (peek(TokenKind.VAR)) {
      return parseField(start, modifiers);
    }
    return parseMethod(start, modifiers);
  }

  private static boolean isValidFieldModifier(Token token) {
    switch (token.kind) {
    case STATIC:
    case NATIVE:
      return true;
    default:
      return false;
    }
  }

  private ParseTree parseField(Token start, ImmutableList<Token> modifiers) {
    validateModifiers(modifiers, Parser::isValidFieldModifier);
    var isStatic = hasModifier(modifiers, TokenKind.STATIC);
    var isNative = hasModifier(modifiers, TokenKind.NATIVE);
    eat(TokenKind.VAR);
    var declarations = parseVariableDeclarations();
    if (isNative) {
      for (var declaration : declarations) {
        if (declaration instanceof VariableDeclarationTree) {
          var initializer = ((VariableDeclarationTree) declaration).initializer;
          if (initializer != null) {
            reportError(
                initializer.start(),
                "Native fields may not have initializers.");
            }
        }
      }
    }
    eat(TokenKind.SEMI_COLON);
    return new FieldDeclarationTree(getRange(start), isStatic, isNative, declarations);
  }

  private static boolean isValidMethodModifier(Token token) {
    switch (token.kind) {
    case STATIC:
    case NATIVE:
      return true;
    case IDENTIFIER:
      return token.asIdentifier().value.equals(JAVASCRIPT);
    default:
      return false;
    }
  }

  private static boolean isValidConstructorModifier(Token token) {
    switch (token.kind) {
    case NATIVE:
      return true;
    case IDENTIFIER:
      return token.asIdentifier().value.equals(JAVASCRIPT);
    default:
      return false;
    }
  }

  private ParseTree parseMethod(Token start, ImmutableList<Token> modifiers) {
    var isStatic = hasModifier(modifiers, TokenKind.STATIC);
    var isNative = hasModifier(modifiers, TokenKind.NATIVE);
    var isJavascript = hasModifier(modifiers, JAVASCRIPT);
    var name = eatId();
    var isCtor = name.value.equals(CONSTRUCTOR);
    var formals = parseParameterListDeclaration(true);
    var returnType = isCtor ? null : parseColonType();
    if (isCtor) {
      if (isStatic) {
        reportError(name, "constructor may not be both 'static'.");
      }
    }
    if (isNative && isJavascript) {
      reportError(name, "Method may not be both 'javascript' and 'native'.");
    }

    ParseTree body;
    if (isNative) {
      body = null;
      eat(TokenKind.SEMI_COLON);
    } else {
      body = parseBlock(isJavascript);
    }
    return new MethodDeclarationTree(getRange(start), returnType, name, formals, isStatic, isNative, isJavascript, body);
  }

  private ParseTree parseColonType() {
    eat(TokenKind.COLON);
    return parseType();
  }

  private ParseTree parseBlock(boolean isJavascript) {
    return isJavascript ? parseJavascriptBlock() : parseBlock();
  }

  private ParseTree parseJavascriptBlock() {
    var javascriptParser = new JavascriptParser(reporter,
        new SourceRange(this.file(), this.getPosition(), this.file().length()));
    var result = javascriptParser.parseBlock();
    this.setPosition(javascriptParser.getPosition());
    return result;
  }

  // Types
  private ParseTree parseType() {
    var start = peek();
    var elementType = parseElementType();
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
    var start = peek();
    var element = parseNamedTypeElement(start, null);
    while (eatOpt(TokenKind.PERIOD)) {
      element = parseNamedTypeElement(start, element);
    }
    return element;
  }

  private NamedTypeTree parseNamedTypeElement(Token start, NamedTypeTree element) {
    var name = eatId();
    var typeArguments = parseTypeArgumentsOpt();
    return new NamedTypeTree(getRange(start), element, name, typeArguments);
  }

  private TypeArgumentListTree parseTypeArgumentsOpt() {
    if (peek(TokenKind.OPEN_ANGLE)) {
      return parseTypeArguments();
    }
    return null;
  }

  private TypeArgumentListTree parseTypeArguments() {
    var start = peek();
    eat(TokenKind.OPEN_ANGLE);
    var typeArguments = parseCommaSeparatedList(this::parseType);
    assert(typeArguments != null);
    return new TypeArgumentListTree(getRange(start), typeArguments);
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
    var start = peek();
    nextToken();
    return new KeywordTypeTree(getRange(start), start);
  }

  private ParseTree parseFunctionType() {
    var start = peek();
    var parameterTypes = parseParenList(() -> parseCommaSeparatedListOpt(this::peekType, this::parseType));
    eat(TokenKind.ARROW);
    var returnType = parseType();
    return new FunctionTypeTree(getRange(start), parameterTypes, returnType);
  }

  // Statements
  private BlockTree parseBlock() {
    var start = peek();
    var statements = parseDelimitedList(TokenKind.OPEN_CURLY, this::parseStatementList, TokenKind.CLOSE_CURLY);
    return new BlockTree(getRange(start), statements);
  }

  private ImmutableList<ParseTree> parseStatementList() {
    return parseList(this::peekStatement, this::parseStatement);
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
    var start = peek();
    eat(TokenKind.VAR);
    var declarations = parseVariableDeclarations();
    eat(TokenKind.SEMI_COLON);
    return new VariableStatementTree(getRange(start), declarations);
  }

  private ImmutableList<ParseTree> parseVariableDeclarations() {
    return parseCommaSeparatedList(this::parseVariableDeclaration);
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
    var start = peek();
    var identifier = eatId();
    return parseVariableDeclaration(start, identifier);
  }

  private VariableDeclarationTree parseVariableDeclaration(Token start, IdentifierToken identifier) {
    var type = parseColonTypeOpt();
    ParseTree initializer = null;
    if (eatOpt(TokenKind.EQUAL)) {
      initializer = parseExpression();
    }
    return new VariableDeclarationTree(getRange(start), identifier, type, initializer);
  }

  private ParseTree parseEmptyStatement() {
    var start = peek();
    eat(TokenKind.SEMI_COLON);
    return new EmptyStatementTree(getRange(start));
  }

  private ParseTree parseIfStatement() {
    var start = peek();
    eat(TokenKind.IF);
    eat(TokenKind.OPEN_PAREN);
    var expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    var ifBody = parseStatement();
    ParseTree elseBody = null;
    if (eatOpt(TokenKind.ELSE)) {
      elseBody = parseStatement();
    }
    return new IfStatementTree(getRange(start), expression, ifBody, elseBody);
  }

  private ParseTree parseDoStatement() {
    var start = peek();
    eat(TokenKind.DO);
    var body = parseStatement();
    eat(TokenKind.WHILE);
    eat(TokenKind.OPEN_PAREN);
    var expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    eat(TokenKind.SEMI_COLON);
    return new DoStatementTree(getRange(start), body, expression);
  }

  private ParseTree parseWhileStatement() {
    var start = peek();
    eat(TokenKind.WHILE);
    eat(TokenKind.OPEN_PAREN);
    var expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new WhileStatementTree(getRange(start), expression, parseStatement());
  }

  private ParseTree parseForStatement() {
    var start = peek();
    eat(TokenKind.FOR);
    eat(TokenKind.OPEN_PAREN);
    switch (peekKind()) {
    case VAR:
      var variableStart = peek();
      eat(TokenKind.VAR);
      var variableDeclaration = parseVariableDeclaration();
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
      var initializer = parseExpression();
      return parseForStatement(start, initializer);
    }
  }

  private ParseTree parseForStatement(Token start, ParseTree initializer) {
    eat(TokenKind.SEMI_COLON);
    var condition = peek(TokenKind.SEMI_COLON) ? null : parseExpression();
    eat(TokenKind.SEMI_COLON);
    var increment = peek(TokenKind.CLOSE_PAREN) ? null : parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new ForStatementTree(getRange(start), initializer, condition, increment, parseStatement());
  }

  private ParseTree parseForIn(Token start, VariableDeclarationTree variableDeclaration) {
    var collection = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new ForInStatementTree(getRange(start), variableDeclaration, collection, parseStatement());
  }

  private ParseTree parseContinueStatement() {
    var start = peek();
    eat(TokenKind.CONTINUE);
    var label = eatIdOpt();
    eat(TokenKind.SEMI_COLON);
    return new ContinueStatementTree(getRange(start), label);
  }

  private ParseTree parseBreakStatement() {
    var start = peek();
    eat(TokenKind.BREAK);
    var label = eatIdOpt();
    eat(TokenKind.SEMI_COLON);
    return new BreakStatementTree(getRange(start), label);
  }

  private ParseTree parseReturnStatement() {
    var start = peek();
    eat(TokenKind.RETURN);
    ParseTree expression = null;
    if (peekExpression()) {
      expression = parseExpression();
    }
    eat(TokenKind.SEMI_COLON);
    return new ReturnStatementTree(getRange(start), expression);
  }

  private ParseTree parseSwitchStatement() {
    var start = peek();
    eat(TokenKind.SWITCH);
    eat(TokenKind.OPEN_PAREN);
    var expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    eat(TokenKind.OPEN_CURLY);
    var caseClauses = new ImmutableList.Builder<ParseTree>();
    ParseTree defaultClause = null;
    while (peekCaseClause()) {
      var clause = parseCaseClause();
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
    var start = peek();
    eat(TokenKind.DEFAULT);
    eat(TokenKind.COLON);
    return new DefaultClauseTree(getRange(start), parseStatementList());
  }

  private ParseTree parseCase() {
    var start = peek();
    eat(TokenKind.CASE);
    var expression = parseExpression();
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
    var start = peek();
    eat(TokenKind.THROW);
    var exception = parseExpression();
    eat(TokenKind.SEMI_COLON);
    return new ThrowStatementTree(getRange(start), exception);
  }

  private ParseTree parseTryStatement() {
    var start = peek();
    eat(TokenKind.TRY);
    var body = parseBlock();
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
    var start = peek();
    eat(TokenKind.CATCH);
    eat(TokenKind.OPEN_PAREN);
    var exception = eatId();
    eat(TokenKind.CLOSE_PAREN);
    var body = parseBlock();
    return new CatchClauseTree(getRange(start), exception, body);
  }

  private ParseTree parseDebuggerStatement() {
    var start = peek();
    eat(TokenKind.DEBUGGER);
    eat(TokenKind.SEMI_COLON);
    return new DebuggerStatementTree(getRange(start));
  }

  private ParseTree parseExpressionStatement() {
    var start = peek();
    var expression = parseExpression();
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
    var left = parseConditionalExpression();
    if (!peekAssignmentOperator()) {
      return left;
    }
    return parseAssignmentExpression(left);
  }

  private ParseTree parseAssignmentExpression(ParseTree left) {
    var operator = nextToken();
    checkForLeftHandSideExpression(left, operator);
    var right = parseExpression();
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
    var start = peek();
    var parameters = parseParameterListDeclaration(false);
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
    var index = 1;
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
    var start = peek();
    var condition = parseLogicalOrExpression();
    if (!eatOpt(TokenKind.QUESTION)) {
      return condition;
    }
    var trueCase = parseExpression();
    eat(TokenKind.COLON);
    var falseCase = parseExpression();
    return new ConditionalExpressionTree(getRange(start), condition, trueCase, falseCase);
  }

  private ParseTree parseLogicalOrExpression() {
    var start = peek();
    var left = parseLogicalAndExpression();
    while (peek(TokenKind.BAR_BAR)) {
      var operator = nextToken();
      var right = parseLogicalAndExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseLogicalAndExpression() {
    var start = peek();
    var left = parseBitwiseOrExpression();
    while (peek(TokenKind.AMPERSAND_AMPERSAND)) {
      var operator = nextToken();
      var right = parseBitwiseOrExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseOrExpression() {
    var start = peek();
    var left = parseBitwiseXorExpression();
    while (peek(TokenKind.BAR)) {
      var operator = nextToken();
      var right = parseBitwiseXorExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseXorExpression() {
    var start = peek();
    var left = parseBitwiseAndExpression();
    while (peek(TokenKind.HAT)) {
      var operator = nextToken();
      var right = parseBitwiseAndExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseBitwiseAndExpression() {
    var start = peek();
    var left = parseEqualityExpression();
    while (peek(TokenKind.AMPERSAND)) {
      var operator = nextToken();
      var right = parseEqualityExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseEqualityExpression() {
    var start = peek();
    var left = parseRelationalExpression();
    while (peekEqualityOperator()) {
      var operator = nextToken();
      var right = parseRelationalExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseRelationalExpression() {
    var start = peek();
    var left = parseInstanceOfExpression();
    while (peekRelationalOperator()) {
      var operator = nextToken();
      var right = parseInstanceOfExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseInstanceOfExpression() {
    var start = peek();
    var left = parseShiftExpression();
    while (peek(TokenKind.INSTANCEOF)) {
      var operator = nextToken();
      var right = parseType();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseShiftExpression() {
    var start = peek();
    var left = parseAdditiveExpression();
    while (peekShiftOperator()) {
      var operator = nextToken();
      var right = parseAdditiveExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseAdditiveExpression() {
    var start = peek();
    var left = parseMultiplicativeExpression();
    while (peekAdditiveOperator()) {
      var operator = nextToken();
      var right = parseMultiplicativeExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseMultiplicativeExpression() {
    var start = peek();
    var left = parseUnaryExpression();
    while (peekMultiplicativeOperator()) {
      var operator = nextToken();
      var right = parseUnaryExpression();
      left = new BinaryExpressionTree(getRange(start), left, operator, right);
    }
    return left;
  }

  private ParseTree parseUnaryExpression() {
    var start = peek();
    if (peekUnaryOperator()) {
      var operator = nextToken();
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
    var start = peek();
    var elements = parseDelimitedList(
        TokenKind.OPEN_SQUARE,
        () -> parseCommaSeparatedListOpt(this::peekExpression, this::parseExpression),
        TokenKind.CLOSE_SQUARE
    );
    return new ArrayLiteralExpressionTree(getRange(start), elements);
  }

  private ParseTree parseThisExpression() {
    var start =eat(TokenKind.THIS);
    return new ThisExpressionTree(getRange(start));
  }

  private ParseTree parseParenExpression() {
    var start = peek();
    eat(TokenKind.OPEN_PAREN);
    var expression = parseExpression();
    eat(TokenKind.CLOSE_PAREN);
    return new ParenExpressionTree(getRange(start), expression);
  }

  private ParseTree parseIdentifier() {
    IdentifierToken name = eatId();
    return new IdentifierExpressionTree(getRange(name), name);
  }

  private ParseTree parsePostfixExpression() {
    var start = peek();
    var left = parseLeftHandSideExpression();
    while (peekPostfixOperator()) {
      left = new PostfixExpressionTree(getRange(start), left, nextToken());
    }
    return left;
  }

  private ParseTree parseLeftHandSideExpression() {
    var start = peek();
    var operand = parseNewExpression();
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
    var start = peek();
    if (eatOpt(TokenKind.NEW)) {
      // TODO: Should be parseTypeName.
      var operand = parseNewExpression();
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
    var start = peek();
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
    var index = parseExpression();
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
    var value = nextToken();
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
    var start = peek();
    var arguments = parseParenList(() -> parseCommaSeparatedListOpt(this::peekExpression, this::parseExpression));
    return new ArgumentsTree(getRange(start), arguments);
  }

  private boolean peekPredefinedName(String name) {
    return peekPredefinedName(0, name);
  }

  private boolean peekPredefinedName(int index, String name) {
    var token = peek();
    return token.isIdentifier() && token.asIdentifier().value.equals(name);
  }

  private IdentifierToken eatId() {
    var token = eat(TokenKind.IDENTIFIER);
    return token.isIdentifier() ? token.asIdentifier() : null;
  }

  private IdentifierToken eatIdOpt() {
    if (peek(TokenKind.IDENTIFIER)) {
      return eatId();
    }
    return null;
  }

}
