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

package omakase.codegeneration;

import com.google.common.collect.ImmutableList;
import omakase.syntax.JavascriptPredefinedNames;
import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.tokens.javascript.IdentifierToken;
import omakase.syntax.trees.*;
import omakase.syntax.trees.javascript.*;
import omakase.syntax.trees.javascript.ArgumentsTree;
import omakase.syntax.trees.javascript.ArrayAccessExpressionTree;
import omakase.syntax.trees.javascript.ArrayLiteralExpressionTree;
import omakase.syntax.trees.javascript.BinaryExpressionTree;
import omakase.syntax.trees.javascript.BlockTree;
import omakase.syntax.trees.javascript.BreakStatementTree;
import omakase.syntax.trees.javascript.CallExpressionTree;
import omakase.syntax.trees.javascript.CaseClauseTree;
import omakase.syntax.trees.javascript.CatchClauseTree;
import omakase.syntax.trees.javascript.ConditionalExpressionTree;
import omakase.syntax.trees.javascript.ContinueStatementTree;
import omakase.syntax.trees.javascript.DebuggerStatementTree;
import omakase.syntax.trees.javascript.DefaultClauseTree;
import omakase.syntax.trees.javascript.DoStatementTree;
import omakase.syntax.trees.javascript.EmptyStatementTree;
import omakase.syntax.trees.javascript.ExpressionStatementTree;
import omakase.syntax.trees.javascript.ForInStatementTree;
import omakase.syntax.trees.javascript.ForStatementTree;
import omakase.syntax.trees.javascript.FormalParameterListTree;
import omakase.syntax.trees.javascript.FunctionExpressionTree;
import omakase.syntax.trees.javascript.IdentifierExpressionTree;
import omakase.syntax.trees.javascript.IfStatementTree;
import omakase.syntax.trees.javascript.LiteralExpressionTree;
import omakase.syntax.trees.javascript.MemberExpressionTree;
import omakase.syntax.trees.javascript.NewExpressionTree;
import omakase.syntax.trees.javascript.ParenExpressionTree;
import omakase.syntax.trees.javascript.PostfixExpressionTree;
import omakase.syntax.trees.javascript.ReturnStatementTree;
import omakase.syntax.trees.javascript.SwitchStatementTree;
import omakase.syntax.trees.javascript.ThisExpressionTree;
import omakase.syntax.trees.javascript.ThrowStatementTree;
import omakase.syntax.trees.javascript.TryStatementTree;
import omakase.syntax.trees.javascript.UnaryExpressionTree;
import omakase.syntax.trees.javascript.VariableDeclarationTree;
import omakase.syntax.trees.javascript.VariableStatementTree;
import omakase.syntax.trees.javascript.WhileStatementTree;

import java.util.List;

/**
 */
public final class JavascriptParseTreeFactory {
  // Expressions and Statements
  public static ArgumentsTree createArguments(ParseTree... arguments) {
    return createArguments(createList(arguments));
  }

  public static ArgumentsTree createArguments(ImmutableList<ParseTree> arguments) {
    return new ArgumentsTree(null, arguments);
  }

  public static ArrayAccessExpressionTree createArrayAccess(ParseTree object, ParseTree member) {
    return new ArrayAccessExpressionTree(null, object,member);
  }

  public static ArrayLiteralExpressionTree createArrayLiteral(ImmutableList<ParseTree> elements) {
    return new ArrayLiteralExpressionTree(null, elements);
  }

  public static ParseTree createBinaryExpression(ParseTree left, Token operator, ParseTree right) {
    return new BinaryExpressionTree(null, left, operator, right);
  }

  public static TokenKind transformOperator(TokenKind operator) {
    switch (operator) {
    case OPEN_ANGLE:
      return TokenKind.JS_OPEN_ANGLE;
    case CLOSE_ANGLE:
      return TokenKind.JS_CLOSE_ANGLE;
    case LESS_EQUAL:
      return TokenKind.JS_LESS_EQUAL;
    case GREATER_EQUAL:
      return TokenKind.JS_GREATER_EQUAL;
    case EQUAL_EQUAL:
      return TokenKind.JS_EQUAL_EQUAL;
    case NOT_EQUAL:
      return TokenKind.JS_NOT_EQUAL;
    case PLUS:
      return TokenKind.JS_PLUS;
    case MINUS:
      return TokenKind.JS_MINUS;
    case PLUS_PLUS:
      return TokenKind.JS_PLUS_PLUS;
    case MINUS_MINUS:
      return TokenKind.JS_MINUS_MINUS;
    case SHIFT_LEFT:
      return TokenKind.JS_SHIFT_LEFT;
    case SHIFT_RIGHT:
      return TokenKind.JS_SHIFT_RIGHT;
    case STAR:
      return TokenKind.JS_STAR;
    case PERCENT:
      return TokenKind.JS_PERCENT;
    case BANG:
      return TokenKind.JS_BANG;
    case TILDE:
      return TokenKind.JS_TILDE;
    case AMPERSAND:
      return TokenKind.JS_AMPERSAND;
    case AMPERSAND_AMPERSAND:
      return TokenKind.JS_AMPERSAND_AMPERSAND;
    case BAR:
      return TokenKind.JS_BAR;
    case BAR_BAR:
      return TokenKind.JS_BAR_BAR;
    case EQUAL:
      return TokenKind.JS_EQUAL;
    case PLUS_EQUAL:
      return TokenKind.JS_PLUS_EQUAL;
    case MINUS_EQUAL:
      return TokenKind.JS_MINUS_EQUAL;
    case STAR_EQUAL:
      return TokenKind.JS_STAR_EQUAL;
    case PERCENT_EQUAL:
      return TokenKind.JS_PERCENT_EQUAL;
    case LEFT_SHIFT_EQUAL:
      return TokenKind.JS_LEFT_SHIFT_EQUAL;
    case RIGHT_SHIFT_EQUAL:
      return TokenKind.JS_RIGHT_SHIFT_EQUAL;
    case AMPERSAND_EQUAL:
      return TokenKind.JS_AMPERSAND_EQUAL;
    case BAR_EQUAL:
      return TokenKind.JS_BAR_EQUAL;
    case HAT_EQUAL:
      return TokenKind.JS_HAT_EQUAL;
    case SLASH:
      return TokenKind.JS_SLASH;
    case SLASH_EQUAL:
      return TokenKind.JS_SLASH_EQUAL;
    case INSTANCEOF:
      return TokenKind.JS_INSTANCEOF;
    case TYPEOF:
      return TokenKind.JS_TYPEOF;
    default:
      throw new RuntimeException("Unexpected operator.");
    }
  }

  public static Token transformOperatorToken(Token operator) {
    return createToken(transformOperator(operator.kind));
  }

  public static BlockTree createBlock(ParseTree... statements) {
    return createBlock(ImmutableList.<ParseTree>copyOf(statements));
  }

  public static BlockTree createBlock(ImmutableList<ParseTree> statements) {
    return new BlockTree(null, statements);
  }

  public static BlockTree createBlock(List<ParseTree> statements) {
    return new BlockTree(null, ImmutableList.<ParseTree>copyOf(statements));
  }

  public static BreakStatementTree createBreak() {
    return new BreakStatementTree(null, null);
  }

  public static ParseTree createCall(ParseTree function, ParseTree... arguments) {
    return createCall(function, createArguments(arguments));
  }

  public static ParseTree createCall(ParseTree function, ArgumentsTree args) {
    return new CallExpressionTree(null, function, args);
  }

  public static CaseClauseTree createCaseClause(ParseTree expression, ImmutableList<ParseTree> statements) {
    return new CaseClauseTree(null, expression, statements);
  }

  public static CatchClauseTree createCatchClause(IdentifierToken identifier, BlockTree block) {
    return new CatchClauseTree(null, identifier, block);
  }

  public static ConditionalExpressionTree createConditional(ParseTree condition, ParseTree left, ParseTree right) {
    return new ConditionalExpressionTree(null, condition, left, right);
  }

  public static ContinueStatementTree createContinue() {
    return new ContinueStatementTree(null, null);
  }

  public static DebuggerStatementTree createDebugger() {
    return new DebuggerStatementTree(null);
  }

  public static DefaultClauseTree createDefaultClause(ImmutableList<ParseTree> statements) {
    return new DefaultClauseTree(null, statements);
  }

  public static DoStatementTree createDoStatement(ParseTree statement, ParseTree condition) {
    return new DoStatementTree(null, statement, condition);
  }

  public static EmptyStatementTree createEmptyStatement() {
    return new EmptyStatementTree(null);
  }

  public static ExpressionStatementTree createExpressionStatement(ParseTree expression) {
    return new ExpressionStatementTree(null, expression);
  }

  public static ForInStatementTree createForInStatement(ParseTree element, ParseTree collection, ParseTree body) {
    return new ForInStatementTree(null, element, collection, body);
  }

  public static ForStatementTree createForStatement(ParseTree initializer, ParseTree condition, ParseTree increment, ParseTree body) {
    return new ForStatementTree(null, initializer, condition, increment, body);
  }

  public static FormalParameterListTree createFormalParameterList() {
    return new FormalParameterListTree(null, ImmutableList.<IdentifierToken>of());
  }

  public static FormalParameterListTree createFormalParameterList(ImmutableList<IdentifierToken> parameters) {
    return new FormalParameterListTree(null, parameters);
  }

  public static IdentifierExpressionTree createIdentifier(String value) {
    return createIdentifier(createIdentifierToken(value));
  }

  public static IdentifierExpressionTree createIdentifier(omakase.syntax.tokens.IdentifierToken identifier) {
    return createIdentifier(identifier.value);
  }

  public static IdentifierExpressionTree createIdentifier(IdentifierToken identifier) {
    return new IdentifierExpressionTree(null, identifier);
  }

  public static IfStatementTree createIfStatement(ParseTree condition, ParseTree ifClause, ParseTree elseClause) {
    return new IfStatementTree(null, condition, ifClause, elseClause);
  }

  public static LiteralExpressionTree createLiteral(Token literal) {
    return new LiteralExpressionTree(null, literal);
  }

  public static MemberExpressionTree createMemberExpression(ParseTree object, String memberName) {
    return new MemberExpressionTree(null, object, createIdentifierToken(memberName));
  }

  public static NewExpressionTree createNew(ParseTree constructor, ArgumentsTree arguments) {
    return new NewExpressionTree(null, constructor, arguments);
  }

  public static LiteralExpressionTree createNull() {
    return new LiteralExpressionTree(null, createToken(TokenKind.NULL));
  }

  public static ParseTree createParenExpression(ParseTree expression) {
    return new ParenExpressionTree(null, expression);
  }

  public static ParseTree createPostfixExpression(ParseTree operand, Token operator) {
    return new PostfixExpressionTree(null, operand, operator);
  }

  public static ReturnStatementTree createReturnStatement(ParseTree value) {
    return new ReturnStatementTree(null, value);
  }

  public static SwitchStatementTree createSwitchStatement(ParseTree expression, ImmutableList<ParseTree> caseClauses) {
    return new SwitchStatementTree(null, expression, caseClauses);
  }

  public static ThisExpressionTree createThis() {
    return new ThisExpressionTree(null);
  }

  public static ThrowStatementTree createThrowStatement(ParseTree expression) {
    return new ThrowStatementTree(null, expression);
  }

  public static TryStatementTree createTryStatement(BlockTree body, CatchClauseTree catchClause, BlockTree finallyClause) {
    return new TryStatementTree(null, body, catchClause, finallyClause);
  }

  public static UnaryExpressionTree createUnaryExpression(Token operator, ParseTree operand) {
    return new UnaryExpressionTree(null, operator, operand);
  }

  public static VariableDeclarationTree createVariableDeclaration(String name, ParseTree initializer) {
    return new VariableDeclarationTree(null, createIdentifierToken(name), initializer);
  }

  public static VariableStatementTree createVariableStatement(ImmutableList<ParseTree> declarations) {
    return new VariableStatementTree(null, declarations);
  }

  public static WhileStatementTree createWhileStatement(ParseTree condition, ParseTree body) {
    return new WhileStatementTree(null, condition, body);
  }

  // Higher Level Helpers
  public static ParseTree createProtoMember(String className, String memberName, ParseTree value) {
    // class.prototype.memberName = value;
    return createAssignmentStatement(
        createDottedName(className, JavascriptPredefinedNames.PROTOTYPE, memberName),
        value);
  }

  public static ParseTree createStaticMember(String className, String memberName, ParseTree value) {
    // class.memberName = value;
    return createAssignmentStatement(
        createDottedName(className, memberName),
        value);
  }

  public static ParseTree createScopedBlock(ImmutableList<ParseTree> statements) {
    // (function() { statements; }())
    return createParenExpression(createCall(createFunction(createFormalParameterList(), createBlock(statements))));
  }

  public static ParseTree createThisBoundFunction(ParseTree function) {
    // (function).bind(this)
    return createCall(createMemberExpression(createParenExpression(function), JavascriptPredefinedNames.BIND), createThis());
  }

  // Low-level Helpers
  public static ImmutableList<ParseTree> createList(ParseTree... arguments) {
    return ImmutableList.copyOf(arguments);
  }
  
  public static ParseTree createFunction(FormalParameterListTree parameters, BlockTree body) {
    return new FunctionExpressionTree(null, null, parameters, body);
  }
  
  public static ParseTree createDottedName(String... names) {
    ParseTree result = null;
    for (String name: names) {
      if (result == null) {
        result = createIdentifier(name);
      } else {
        result = createMemberExpression(result, name);
      }
    }
    return result;
  }

  public static ParseTree createAssignmentStatement(ParseTree left, ParseTree right) {
    return createExpressionStatement(
        createBinaryExpression(left, createToken(TokenKind.JS_EQUAL), right)
    );
  }

  public static IdentifierToken createIdentifierToken(String value) {
    return new IdentifierToken(null, value);
  }

  public static Token createToken(TokenKind kind) {
    return new Token(kind, null);
  }
}
