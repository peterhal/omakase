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
import omakase.syntax.trees.*;

/**
 *
 */
public class ParseTreeVisitor {

  protected void visitList(ImmutableList<? extends ParseTree> trees) {
    for (ParseTree tree: trees) {
      visitAny(tree);
    }
  }

  public void visitAny(ParseTree tree) {
    if (tree == null) {
      return;
    }

    switch (tree.kind) {
    case ARGUMENTS:
      visit(tree.asArguments());
      break;
    case ARRAY_ACCESS_EXPRESSION:
      visit(tree.asArrayAccessExpression());
      break;
    case ARRAY_LITERAL_EXPRESSION:
      visit(tree.asArrayLiteralExpression());
      break;
    case ARRAY_TYPE:
      visit(tree.asArrayType());
      break;
    case BINARY_EXPRESSION:
      visit(tree.asBinaryExpression());
      break;
    case BLOCK:
      visit(tree.asBlock());
      break;
    case BREAK_STATEMENT:
      visit(tree.asBreakStatement());
      break;
    case CALL_EXPRESSION:
      visit(tree.asCallExpression());
      break;
    case CASE_CLAUSE:
      visit(tree.asCaseClause());
      break;
    case CATCH_CLAUSE:
      visit(tree.asCatchClause());
      break;
    case CLASS_DECLARATION:
      visit(tree.asClassDeclaration());
      break;
    case CONDITIONAL_EXPRESSION:
      visit(tree.asConditionalExpression());
      break;
    case CONTINUE_STATEMENT:
      visit(tree.asContinueStatement());
      break;
    case DEBUGGER_STATEMENT:
      visit(tree.asDebuggerStatement());
      break;
    case DEFAULT_CLAUSE:
      visit(tree.asDefaultClause());
      break;
    case DO_STATEMENT:
      visit(tree.asDoStatement());
      break;
    case EMPTY_STATEMENT:
      visit(tree.asEmptyStatement());
      break;
    case EXPRESSION_STATEMENT:
      visit(tree.asExpressionStatement());
      break;
    case FIELD_DECLARATION:
      visit(tree.asFieldDeclaration());
      break;
    case FOR_IN_STATEMENT:
      visit(tree.asForInStatement());
      break;
    case FOR_STATEMENT:
      visit(tree.asForStatement());
      break;
    case FORMAL_PARAMETER_LIST:
      visit(tree.asFormalParameterList());
      break;
    case FUNCTION_EXPRESSION:
      visit(tree.asFunctionExpression());
      break;
    case FUNCTION_DECLARATION:
      visit(tree.asFunctionDeclaration());
      break;
    case FUNCTION_TYPE:
      visit(tree.asFunctionType());
      break;
    case IDENTIFIER_EXPRESSION:
      visit(tree.asIdentifierExpression());
      break;
    case IF_STATEMENT:
      visit(tree.asIfStatement());
      break;
    case KEYWORD_TYPE:
      visit(tree.asKeywordType());
      break;
    case LITERAL_EXPRESSION:
      visit(tree.asLiteralExpression());
      break;
    case MEMBER_EXPRESSION:
      visit(tree.asMemberExpression());
      break;
    case METHOD_DECLARATION:
      visit(tree.asMethodDeclaration());
      break;
    case NAMED_TYPE:
      visit(tree.asNamedType());
      break;
    case NEW_EXPRESSION:
      visit(tree.asNewExpression());
      break;
    case NULLABLE_TYPE:
      visit(tree.asNullableType());
      break;
    case PARAMETER_DECLARATION:
      visit(tree.asParameterDeclaration());
      break;
    case PAREN_EXPRESSION:
      visit(tree.asParenExpression());
      break;
    case POSTFIX_EXPRESSION:
      visit(tree.asPostfixExpression());
      break;
    case RETURN_STATEMENT:
      visit(tree.asReturnStatement());
      break;
    case SOURCE_FILE:
      visit(tree.asSourceFile());
      break;
    case SWITCH_STATEMENT:
      visit(tree.asSwitchStatement());
      break;
    case THIS_EXPRESSION:
      visit(tree.asThisExpression());
      break;
    case THROW_STATEMENT:
      visit(tree.asThrowStatement());
      break;
    case TRY_STATEMENT:
      visit(tree.asTryStatement());
      break;
    case TYPE_ARGUMENT_LIST:
      visit(tree.asTypeArgumentList());
      break;
    case TYPE_PARAMETER_DECLARATION:
      visit(tree.asTypeParameterDeclaration());
      break;
    case UNARY_EXPRESSION:
      visit(tree.asUnaryExpression());
      break;
    case VARIABLE_DECLARATION:
      visit(tree.asVariableDeclaration());
      break;
    case VARIABLE_STATEMENT:
      visit(tree.asVariableStatement());
      break;
    case WHILE_STATEMENT:
      visit(tree.asWhileStatement());
      break;
    case JAVASCRIPT_ARGUMENTS:
      visit(tree.asJavascriptArguments());
      break;
    case JAVASCRIPT_ARRAY_ACCESS_EXPRESSION:
      visit(tree.asJavascriptArrayAccessExpression());
      break;
    case JAVASCRIPT_ARRAY_LITERAL_EXPRESSION:
      visit(tree.asJavascriptArrayLiteralExpression());
      break;
    case JAVASCRIPT_BINARY_EXPRESSION:
      visit(tree.asJavascriptBinaryExpression());
      break;
    case JAVASCRIPT_BLOCK:
      visit(tree.asJavascriptBlock());
      break;
    case JAVASCRIPT_BREAK_STATEMENT:
      visit(tree.asJavascriptBreakStatement());
      break;
    case JAVASCRIPT_CALL_EXPRESSION:
      visit(tree.asJavascriptCallExpression());
      break;
    case JAVASCRIPT_CASE_CLAUSE:
      visit(tree.asJavascriptCaseClause());
      break;
    case JAVASCRIPT_CATCH_CLAUSE:
      visit(tree.asJavascriptCatchClause());
      break;
    case JAVASCRIPT_COMMA_EXPRESSION:
      visit(tree.asJavascriptCommaExpression());
      break;
    case JAVASCRIPT_CONDITIONAL_EXPRESSION:
      visit(tree.asJavascriptConditionalExpression());
      break;
    case JAVASCRIPT_CONTINUE_STATEMENT:
      visit(tree.asJavascriptContinueStatement());
      break;
    case JAVASCRIPT_DEBUGGER_STATEMENT:
      visit(tree.asJavascriptDebuggerStatement());
      break;
    case JAVASCRIPT_DEFAULT_CLAUSE:
      visit(tree.asJavascriptDefaultClause());
      break;
    case JAVASCRIPT_DO_STATEMENT:
      visit(tree.asJavascriptDoStatement());
      break;
    case JAVASCRIPT_ELISION:
      visit(tree.asJavascriptElision());
      break;
    case JAVASCRIPT_EMPTY_STATEMENT:
      visit(tree.asJavascriptEmptyStatement());
      break;
    case JAVASCRIPT_EXPRESSION_STATEMENT:
      visit(tree.asJavascriptExpressionStatement());
      break;
    case JAVASCRIPT_FOR_IN_STATEMENT:
      visit(tree.asJavascriptForInStatement());
      break;
    case JAVASCRIPT_FOR_STATEMENT:
      visit(tree.asJavascriptForStatement());
      break;
    case JAVASCRIPT_FORMAL_PARAMETER_LIST:
      visit(tree.asJavascriptFormalParameterList());
      break;
    case JAVASCRIPT_FUNCTION_EXPRESSION:
      visit(tree.asJavascriptFunctionExpression());
      break;
    case JAVASCRIPT_GET_ACCESSOR:
      visit(tree.asJavascriptGetAccessor());
      break;
    case JAVASCRIPT_IDENTIFIER_EXPRESSION:
      visit(tree.asJavascriptIdentifierExpression());
      break;
    case JAVASCRIPT_IF_STATEMENT:
      visit(tree.asJavascriptIfStatement());
      break;
    case JAVASCRIPT_LABELLED_STATEMENT:
      visit(tree.asJavascriptLabelledStatement());
      break;
    case JAVASCRIPT_LITERAL_EXPRESSION:
      visit(tree.asJavascriptLiteralExpression());
      break;
    case JAVASCRIPT_MEMBER_EXPRESSION:
      visit(tree.asJavascriptMemberExpression());
      break;
    case JAVASCRIPT_NEW_EXPRESSION:
      visit(tree.asJavascriptNewExpression());
      break;
    case JAVASCRIPT_OBJECT_LITERAL_EXPRESSION:
      visit(tree.asJavascriptObjectLiteralExpression());
      break;
    case JAVASCRIPT_PAREN_EXPRESSION:
      visit(tree.asJavascriptParenExpression());
      break;
    case JAVASCRIPT_POSTFIX_EXPRESSION:
      visit(tree.asJavascriptPostfixExpression());
      break;
    case JAVASCRIPT_PROGRAM:
      visit(tree.asJavascriptProgram());
      break;
    case JAVASCRIPT_PROPERTY_ASSIGNMENT:
      visit(tree.asJavascriptPropertyAssignment());
      break;
    case JAVASCRIPT_RETURN_STATEMENT:
      visit(tree.asJavascriptReturnStatement());
      break;
    case JAVASCRIPT_SET_ACCESSOR:
      visit(tree.asJavascriptSetAccessor());
      break;
    case JAVASCRIPT_SWITCH_STATEMENT:
      visit(tree.asJavascriptSwitchStatement());
      break;
    case JAVASCRIPT_THIS_EXPRESSION:
      visit(tree.asJavascriptThisExpression());
      break;
    case JAVASCRIPT_THROW_STATEMENT:
      visit(tree.asJavascriptThrowStatement());
      break;
    case JAVASCRIPT_TRY_STATEMENT:
      visit(tree.asJavascriptTryStatement());
      break;
    case JAVASCRIPT_UNARY_EXPRESSION:
      visit(tree.asJavascriptUnaryExpression());
      break;
    case JAVASCRIPT_VARIABLE_DECLARATION:
      visit(tree.asJavascriptVariableDeclaration());
      break;
    case JAVASCRIPT_VARIABLE_STATEMENT:
      visit(tree.asJavascriptVariableStatement());
      break;
    case JAVASCRIPT_WHILE_STATEMENT:
      visit(tree.asJavascriptWhileStatement());
      break;
    case JAVASCRIPT_WITH_STATEMENT:
      visit(tree.asJavascriptWithStatement());
      break;
    }
  }

  protected void visit(ArgumentsTree tree) {
    visitList(tree.arguments);
  }

  protected void visit(ArrayAccessExpressionTree tree) {
    visitAny(tree.object);
    visitAny(tree.member);
  }

  protected void visit(ArrayLiteralExpressionTree tree) {
    visitList(tree.elements);
  }

  protected void visit(ArrayTypeTree tree) {
    visitAny(tree.elementType);
  }

  protected void visit(BinaryExpressionTree tree) {
    visitAny(tree.left);
    visitAny(tree.right);
  }

  protected void visit(BlockTree tree) {
    visitList(tree.statements);
  }

  protected void visit(BreakStatementTree tree) {
  }

  protected void visit(CallExpressionTree tree) {
    visitAny(tree.function);
    visitAny(tree.arguments);
  }

  protected void visit(CaseClauseTree tree) {
    visitAny(tree.expression);
    visitList(tree.statements);
  }

  protected void visit(CatchClauseTree tree) {
    visitAny(tree.block);
  }

  protected void visit(ClassDeclarationTree tree) {
    visitList(tree.typeParameters);
    visitList(tree.members);
  }

  protected void visit(ConditionalExpressionTree tree) {
    visitAny(tree.condition);
    visitAny(tree.left);
    visitAny(tree.right);
  }

  protected void visit(ContinueStatementTree tree) {
  }

  protected void visit(DebuggerStatementTree tree) {
  }

  protected void visit(DefaultClauseTree tree) {
    visitList(tree.statements);
  }

  protected void visit(DoStatementTree tree) {
    visitAny(tree.statement);
    visitAny(tree.condition);
  }

  protected void visit(EmptyStatementTree tree) {
  }

  protected void visit(ExpressionStatementTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(FieldDeclarationTree tree) {
    visitList(tree.declarations);
  }

  protected void visit(ForInStatementTree tree) {
    visitAny(tree.element);
    visitAny(tree.collection);
    visitAny(tree.body);
  }

  protected void visit(ForStatementTree tree) {
    visitAny(tree.initializer);
    visitAny(tree.condition);
    visitAny(tree.increment);
    visitAny(tree.body);
  }

  protected void visit(FormalParameterListTree tree) {
    visitList(tree.parameters);
  }

  protected void visit(FunctionExpressionTree tree) {
    visitAny(tree.parameters);
    visitAny(tree.body);
  }

  protected void visit(FunctionDeclarationTree tree) {
    visitAny(tree.formals);
    visitAny(tree.returnType);
    visitAny(tree.body);
  }

  protected void visit(FunctionTypeTree tree) {
    visitList(tree.argumentTypes);
    visitAny(tree.returnType);
  }

  protected void visit(IdentifierExpressionTree tree) {
  }

  protected void visit(IfStatementTree tree) {
    visitAny(tree.condition);
    visitAny(tree.ifClause);
    visitAny(tree.elseClause);
  }

  protected void visit(KeywordTypeTree tree) {
  }

  protected void visit(LiteralExpressionTree tree) {
  }

  protected void visit(MemberExpressionTree tree) {
    visitAny(tree.object);
  }

  protected void visit(MethodDeclarationTree tree) {
    visitAny(tree.returnType);
    visitAny(tree.formals);
    visitAny(tree.body);
  }

  protected void visit(NamedTypeTree tree) {
    visitAny(tree.element);
    visitAny(tree.typeArguments);
  }

  protected void visit(NewExpressionTree tree) {
    visitAny(tree.constructor);
    visitAny(tree.arguments);
  }

  protected void visit(NullableTypeTree tree) {
    visitAny(tree.elementType);
  }

  protected void visit(ParameterDeclarationTree tree) {
    visitAny(tree.type);
  }

  protected void visit(ParenExpressionTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(PostfixExpressionTree tree) {
    visitAny(tree.operand);
  }

  protected void visit(ReturnStatementTree tree) {
    visitAny(tree.value);
  }

  protected void visit(SourceFileTree tree) {
    visitList(tree.declarations);
  }

  protected void visit(SwitchStatementTree tree) {
    visitAny(tree.expression);
    visitList(tree.caseClauses);
  }

  protected void visit(ThisExpressionTree tree) {
  }

  protected void visit(ThrowStatementTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(TryStatementTree tree) {
    visitAny(tree.body);
    visitAny(tree.catchClause);
    visitAny(tree.finallyClause);
  }

  protected void visit(TypeArgumentListTree tree) {
    visitList(tree.typeArguments);
  }

  protected void visit(TypeParameterDeclarationTree tree) {
    visitAny(tree.bounds);
  }

  protected void visit(UnaryExpressionTree tree) {
    visitAny(tree.operand);
  }

  protected void visit(VariableDeclarationTree tree) {
    visitAny(tree.type);
    visitAny(tree.initializer);
  }

  protected void visit(VariableStatementTree tree) {
    visitList(tree.declarations);
  }

  protected void visit(WhileStatementTree tree) {
    visitAny(tree.condition);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.ArgumentsTree tree) {
    visitList(tree.arguments);
  }

  protected void visit(omakase.syntax.trees.javascript.ArrayAccessExpressionTree tree) {
    visitAny(tree.object);
    visitAny(tree.member);
  }

  protected void visit(omakase.syntax.trees.javascript.ArrayLiteralExpressionTree tree) {
    visitList(tree.elements);
  }

  protected void visit(omakase.syntax.trees.javascript.BinaryExpressionTree tree) {
    visitAny(tree.left);
    visitAny(tree.right);
  }

  protected void visit(omakase.syntax.trees.javascript.BlockTree tree) {
    visitList(tree.statements);
  }

  protected void visit(omakase.syntax.trees.javascript.BreakStatementTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.CallExpressionTree tree) {
    visitAny(tree.function);
    visitAny(tree.arguments);
  }

  protected void visit(omakase.syntax.trees.javascript.CaseClauseTree tree) {
    visitAny(tree.expression);
    visitList(tree.statements);
  }

  protected void visit(omakase.syntax.trees.javascript.CatchClauseTree tree) {
    visitAny(tree.block);
  }

  protected void visit(omakase.syntax.trees.javascript.CommaExpressionTree tree) {
    visitList(tree.expressions);
  }

  protected void visit(omakase.syntax.trees.javascript.ConditionalExpressionTree tree) {
    visitAny(tree.condition);
    visitAny(tree.left);
    visitAny(tree.right);
  }

  protected void visit(omakase.syntax.trees.javascript.ContinueStatementTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.DebuggerStatementTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.DefaultClauseTree tree) {
    visitList(tree.statements);
  }

  protected void visit(omakase.syntax.trees.javascript.DoStatementTree tree) {
    visitAny(tree.statement);
    visitAny(tree.condition);
  }

  protected void visit(omakase.syntax.trees.javascript.ElisionTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.EmptyStatementTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.ExpressionStatementTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(omakase.syntax.trees.javascript.ForInStatementTree tree) {
    visitAny(tree.element);
    visitAny(tree.collection);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.ForStatementTree tree) {
    visitAny(tree.initializer);
    visitAny(tree.condition);
    visitAny(tree.increment);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.FormalParameterListTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.FunctionExpressionTree tree) {
    visitAny(tree.parameters);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.GetAccessorTree tree) {
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.IdentifierExpressionTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.IfStatementTree tree) {
    visitAny(tree.condition);
    visitAny(tree.ifClause);
    visitAny(tree.elseClause);
  }

  protected void visit(omakase.syntax.trees.javascript.LabelledStatementTree tree) {
    visitAny(tree.statement);
  }

  protected void visit(omakase.syntax.trees.javascript.LiteralExpressionTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.MemberExpressionTree tree) {
    visitAny(tree.object);
  }

  protected void visit(omakase.syntax.trees.javascript.NewExpressionTree tree) {
    visitAny(tree.constructor);
    visitAny(tree.arguments);
  }

  protected void visit(omakase.syntax.trees.javascript.ObjectLiteralExpressionTree tree) {
    visitList(tree.initializers);
  }

  protected void visit(omakase.syntax.trees.javascript.ParenExpressionTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(omakase.syntax.trees.javascript.PostfixExpressionTree tree) {
    visitAny(tree.operand);
  }

  protected void visit(omakase.syntax.trees.javascript.ProgramTree tree) {
    visitList(tree.sourceElements);
  }

  protected void visit(omakase.syntax.trees.javascript.PropertyAssignmentTree tree) {
    visitAny(tree.value);
  }

  protected void visit(omakase.syntax.trees.javascript.ReturnStatementTree tree) {
    visitAny(tree.value);
  }

  protected void visit(omakase.syntax.trees.javascript.SetAccessorTree tree) {
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.SwitchStatementTree tree) {
    visitAny(tree.expression);
    visitList(tree.caseClauses);
  }

  protected void visit(omakase.syntax.trees.javascript.ThisExpressionTree tree) {
  }

  protected void visit(omakase.syntax.trees.javascript.ThrowStatementTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(omakase.syntax.trees.javascript.TryStatementTree tree) {
    visitAny(tree.body);
    visitAny(tree.catchClause);
    visitAny(tree.finallyClause);
  }

  protected void visit(omakase.syntax.trees.javascript.UnaryExpressionTree tree) {
    visitAny(tree.operand);
  }

  protected void visit(omakase.syntax.trees.javascript.VariableDeclarationTree tree) {
    visitAny(tree.initializer);
  }

  protected void visit(omakase.syntax.trees.javascript.VariableStatementTree tree) {
    visitList(tree.declarations);
  }

  protected void visit(omakase.syntax.trees.javascript.WhileStatementTree tree) {
    visitAny(tree.condition);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.WithStatementTree tree) {
    visitAny(tree.expression);
    visitAny(tree.body);
  }
}
