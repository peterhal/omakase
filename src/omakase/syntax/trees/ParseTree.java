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

package omakase.syntax.trees;

import omakase.util.SourceLocation;
import omakase.util.SourceRange;

/**
 *
 */
public class ParseTree {
  public final SourceRange location;
  public final ParseTreeKind kind;

  public ParseTree(SourceRange range, ParseTreeKind kind) {
    location = range;
    this.kind = kind;
  }

  public SourceLocation start() {
    return location.start;
  }

  public ArgumentsTree asArguments() {
    return (ArgumentsTree) this;
  }

  public ArrayAccessExpressionTree asArrayAccessExpression() {
    return (ArrayAccessExpressionTree) this;
  }

  public ArrayLiteralExpressionTree asArrayLiteralExpression() {
    return (ArrayLiteralExpressionTree) this;
  }

  public BinaryExpressionTree asBinaryExpression() {
    return (BinaryExpressionTree) this;
  }

  public BlockTree asBlock() {
    return (BlockTree) this;
  }

  public BreakStatementTree asBreakStatement() {
    return (BreakStatementTree) this;
  }

  public CallExpressionTree asCallExpression() {
    return (CallExpressionTree) this;
  }

  public CaseClauseTree asCaseClause() {
    return (CaseClauseTree) this;
  }

  public CatchClauseTree asCatchClause() {
    return (CatchClauseTree) this;
  }

  public ClassDeclarationTree asClassDeclaration() {
    return (ClassDeclarationTree) this;
  }

  public ConditionalExpressionTree asConditionalExpression() {
    return (ConditionalExpressionTree) this;
  }

  public ContinueStatementTree asContinueStatement() {
    return (ContinueStatementTree) this;
  }

  public DebuggerStatementTree asDebuggerStatement() {
    return (DebuggerStatementTree) this;
  }

  public DefaultClauseTree asDefaultClause() {
    return (DefaultClauseTree) this;
  }

  public DoStatementTree asDoStatement() {
    return (DoStatementTree) this;
  }

  public EmptyStatementTree asEmptyStatement() {
    return (EmptyStatementTree) this;
  }

  public ExpressionStatementTree asExpressionStatement() {
    return (ExpressionStatementTree) this;
  }

  public ForInStatementTree asForInStatement() {
    return (ForInStatementTree) this;
  }

  public ForStatementTree asForStatement() {
    return (ForStatementTree) this;
  }

  public FormalParameterListTree asFormalParameterList() {
    return (FormalParameterListTree) this;
  }

  public FunctionExpressionTree asFunctionExpression() {
    return (FunctionExpressionTree) this;
  }

  public IdentifierExpressionTree asIdentifierExpression() {
    return (IdentifierExpressionTree) this;
  }

  public IfStatementTree asIfStatement() {
    return (IfStatementTree) this;
  }

  public LiteralExpressionTree asLiteralExpression() {
    return (LiteralExpressionTree) this;
  }

  public MethodDeclarationTree asMethodDeclaration() {
    return (MethodDeclarationTree) this;
  }

  public NewExpressionTree asNewExpression() {
    return (NewExpressionTree) this;
  }

  public ParameterDeclarationTree asParameterDeclaration() {
    return (ParameterDeclarationTree) this;
  }

  public ParenExpressionTree asParenExpression() {
    return (ParenExpressionTree) this;
  }

  public PostfixExpressionTree asPostfixExpression() {
    return (PostfixExpressionTree) this;
  }

  public ReturnStatementTree asReturnStatement() {
    return (ReturnStatementTree) this;
  }

  public SourceFileTree asSourceFile() {
    return (SourceFileTree) this;
  }

  public SwitchStatementTree asSwitchStatement() {
    return (SwitchStatementTree) this;
  }

  public ThisExpressionTree asThisExpression() {
    return (ThisExpressionTree) this;
  }

  public ThrowStatementTree asThrowStatement() {
    return (ThrowStatementTree) this;
  }

  public TryStatementTree asTryStatement() {
    return (TryStatementTree) this;
  }

  public UnaryExpressionTree asUnaryExpression() {
    return (UnaryExpressionTree) this;
  }

  public VariableDeclarationTree asVariableDeclaration() {
    return (VariableDeclarationTree) this;
  }

  public VariableStatementTree asVariableStatement() {
    return (VariableStatementTree) this;
  }

  public WhileStatementTree asWhileStatement() {
    return (WhileStatementTree) this;
  }

  public omakase.syntax.trees.javascript.ArgumentsTree asJavascriptArguments() {
    return (omakase.syntax.trees.javascript.ArgumentsTree) this;
  }

  public omakase.syntax.trees.javascript.ArrayAccessExpressionTree asJavascriptArrayAccessExpression() {
    return (omakase.syntax.trees.javascript.ArrayAccessExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ArrayLiteralExpressionTree asJavascriptArrayLiteralExpression() {
    return (omakase.syntax.trees.javascript.ArrayLiteralExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.BinaryExpressionTree asJavascriptBinaryExpression() {
    return (omakase.syntax.trees.javascript.BinaryExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.BlockTree asJavascriptBlock() {
    return (omakase.syntax.trees.javascript.BlockTree) this;
  }

  public omakase.syntax.trees.javascript.BreakStatementTree asJavascriptBreakStatement() {
    return (omakase.syntax.trees.javascript.BreakStatementTree) this;
  }

  public omakase.syntax.trees.javascript.CallExpressionTree asJavascriptCallExpression() {
    return (omakase.syntax.trees.javascript.CallExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.CaseClauseTree asJavascriptCaseClause() {
    return (omakase.syntax.trees.javascript.CaseClauseTree) this;
  }

  public omakase.syntax.trees.javascript.CatchClauseTree asJavascriptCatchClause() {
    return (omakase.syntax.trees.javascript.CatchClauseTree) this;
  }

  public omakase.syntax.trees.javascript.CommaExpressionTree asJavascriptCommaExpression() {
    return (omakase.syntax.trees.javascript.CommaExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ConditionalExpressionTree asJavascriptConditionalExpression() {
    return (omakase.syntax.trees.javascript.ConditionalExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ContinueStatementTree asJavascriptContinueStatement() {
    return (omakase.syntax.trees.javascript.ContinueStatementTree) this;
  }

  public omakase.syntax.trees.javascript.DebuggerStatementTree asJavascriptDebuggerStatement() {
    return (omakase.syntax.trees.javascript.DebuggerStatementTree) this;
  }

  public omakase.syntax.trees.javascript.DefaultClauseTree asJavascriptDefaultClause() {
    return (omakase.syntax.trees.javascript.DefaultClauseTree) this;
  }

  public omakase.syntax.trees.javascript.DoStatementTree asJavascriptDoStatement() {
    return (omakase.syntax.trees.javascript.DoStatementTree) this;
  }

  public omakase.syntax.trees.javascript.ElisionTree asJavascriptElision() {
    return (omakase.syntax.trees.javascript.ElisionTree) this;
  }

  public omakase.syntax.trees.javascript.EmptyStatementTree asJavascriptEmptyStatement() {
    return (omakase.syntax.trees.javascript.EmptyStatementTree) this;
  }

  public omakase.syntax.trees.javascript.ExpressionStatementTree asJavascriptExpressionStatement() {
    return (omakase.syntax.trees.javascript.ExpressionStatementTree) this;
  }

  public omakase.syntax.trees.javascript.ForInStatementTree asJavascriptForInStatement() {
    return (omakase.syntax.trees.javascript.ForInStatementTree) this;
  }

  public omakase.syntax.trees.javascript.ForStatementTree asJavascriptForStatement() {
    return (omakase.syntax.trees.javascript.ForStatementTree) this;
  }

  public omakase.syntax.trees.javascript.FormalParameterListTree asJavascriptFormalParameterList() {
    return (omakase.syntax.trees.javascript.FormalParameterListTree) this;
  }

  public omakase.syntax.trees.javascript.FunctionExpressionTree asJavascriptFunctionExpression() {
    return (omakase.syntax.trees.javascript.FunctionExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.GetAccessorTree asJavascriptGetAccessor() {
    return (omakase.syntax.trees.javascript.GetAccessorTree) this;
  }

  public omakase.syntax.trees.javascript.IdentifierExpressionTree asJavascriptIdentifierExpression() {
    return (omakase.syntax.trees.javascript.IdentifierExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.IfStatementTree asJavascriptIfStatement() {
    return (omakase.syntax.trees.javascript.IfStatementTree) this;
  }

  public omakase.syntax.trees.javascript.LabelledStatementTree asJavascriptLabelledStatement() {
    return (omakase.syntax.trees.javascript.LabelledStatementTree) this;
  }

  public omakase.syntax.trees.javascript.LiteralExpressionTree asJavascriptLiteralExpression() {
    return (omakase.syntax.trees.javascript.LiteralExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.MemberExpressionTree asJavascriptMemberExpression() {
    return (omakase.syntax.trees.javascript.MemberExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.NewExpressionTree asJavascriptNewExpression() {
    return (omakase.syntax.trees.javascript.NewExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ObjectLiteralExpressionTree asJavascriptObjectLiteralExpression() {
    return (omakase.syntax.trees.javascript.ObjectLiteralExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ParenExpressionTree asJavascriptParenExpression() {
    return (omakase.syntax.trees.javascript.ParenExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.PostfixExpressionTree asJavascriptPostfixExpression() {
    return (omakase.syntax.trees.javascript.PostfixExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ProgramTree asJavascriptProgram() {
    return (omakase.syntax.trees.javascript.ProgramTree) this;
  }

  public omakase.syntax.trees.javascript.PropertyAssignmentTree asJavascriptPropertyAssignment() {
    return (omakase.syntax.trees.javascript.PropertyAssignmentTree) this;
  }

  public omakase.syntax.trees.javascript.ReturnStatementTree asJavascriptReturnStatement() {
    return (omakase.syntax.trees.javascript.ReturnStatementTree) this;
  }

  public omakase.syntax.trees.javascript.SetAccessorTree asJavascriptSetAccessor() {
    return (omakase.syntax.trees.javascript.SetAccessorTree) this;
  }

  public omakase.syntax.trees.javascript.SwitchStatementTree asJavascriptSwitchStatement() {
    return (omakase.syntax.trees.javascript.SwitchStatementTree) this;
  }

  public omakase.syntax.trees.javascript.ThisExpressionTree asJavascriptThisExpression() {
    return (omakase.syntax.trees.javascript.ThisExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ThrowStatementTree asJavascriptThrowStatement() {
    return (omakase.syntax.trees.javascript.ThrowStatementTree) this;
  }

  public omakase.syntax.trees.javascript.TryStatementTree asJavascriptTryStatement() {
    return (omakase.syntax.trees.javascript.TryStatementTree) this;
  }

  public omakase.syntax.trees.javascript.UnaryExpressionTree asJavascriptUnaryExpression() {
    return (omakase.syntax.trees.javascript.UnaryExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.VariableDeclarationTree asJavascriptVariableDeclaration() {
    return (omakase.syntax.trees.javascript.VariableDeclarationTree) this;
  }

  public omakase.syntax.trees.javascript.VariableStatementTree asJavascriptVariableStatement() {
    return (omakase.syntax.trees.javascript.VariableStatementTree) this;
  }

  public omakase.syntax.trees.javascript.WhileStatementTree asJavascriptWhileStatement() {
    return (omakase.syntax.trees.javascript.WhileStatementTree) this;
  }

  public omakase.syntax.trees.javascript.WithStatementTree asJavascriptWithStatement() {
    return (omakase.syntax.trees.javascript.WithStatementTree) this;
  }

  public boolean isArguments() {
    return this.kind == ParseTreeKind.ARGUMENTS;
  }

  public boolean isArrayAccessExpression() {
    return this.kind == ParseTreeKind.ARRAY_ACCESS_EXPRESSION;
  }

  public boolean isArrayLiteralExpression() {
    return this.kind == ParseTreeKind.ARRAY_LITERAL_EXPRESSION;
  }

  public boolean isBinaryExpression() {
    return this.kind == ParseTreeKind.BINARY_EXPRESSION;
  }

  public boolean isBlock() {
    return this.kind == ParseTreeKind.BLOCK;
  }

  public boolean isBreakStatement() {
    return this.kind == ParseTreeKind.BREAK_STATEMENT;
  }

  public boolean isCallExpression() {
    return this.kind == ParseTreeKind.CALL_EXPRESSION;
  }

  public boolean isCaseClause() {
    return this.kind == ParseTreeKind.CASE_CLAUSE;
  }

  public boolean isCatchClause() {
    return this.kind == ParseTreeKind.CATCH_CLAUSE;
  }

  public boolean isClassDeclaration() {
    return this.kind == ParseTreeKind.CLASS_DECLARATION;
  }

  public boolean isConditionalExpression() {
    return this.kind == ParseTreeKind.CONDITIONAL_EXPRESSION;
  }

  public boolean isContinueStatement() {
    return this.kind == ParseTreeKind.CONTINUE_STATEMENT;
  }

  public boolean isDebuggerStatement() {
    return this.kind == ParseTreeKind.DEBUGGER_STATEMENT;
  }

  public boolean isDefaultClause() {
    return this.kind == ParseTreeKind.DEFAULT_CLAUSE;
  }

  public boolean isDoStatement() {
    return this.kind == ParseTreeKind.DO_STATEMENT;
  }

  public boolean isEmptyStatement() {
    return this.kind == ParseTreeKind.EMPTY_STATEMENT;
  }

  public boolean isExpressionStatement() {
    return this.kind == ParseTreeKind.EXPRESSION_STATEMENT;
  }

  public boolean isForInStatement() {
    return this.kind == ParseTreeKind.FOR_IN_STATEMENT;
  }

  public boolean isForStatement() {
    return this.kind == ParseTreeKind.FOR_STATEMENT;
  }

  public boolean isFormalParameterList() {
    return this.kind == ParseTreeKind.FORMAL_PARAMETER_LIST;
  }

  public boolean isFunctionExpression() {
    return this.kind == ParseTreeKind.FUNCTION_EXPRESSION;
  }

  public boolean isIdentifierExpression() {
    return this.kind == ParseTreeKind.IDENTIFIER_EXPRESSION;
  }

  public boolean isIfStatement() {
    return this.kind == ParseTreeKind.IF_STATEMENT;
  }

  public boolean isLiteralExpression() {
    return this.kind == ParseTreeKind.LITERAL_EXPRESSION;
  }

  public boolean isMethodDeclaration() {
    return this.kind == ParseTreeKind.METHOD_DECLARATION;
  }

  public boolean isNewExpression() {
    return this.kind == ParseTreeKind.NEW_EXPRESSION;
  }

  public boolean isParameterDeclaration() {
    return this.kind == ParseTreeKind.PARAMETER_DECLARATION;
  }

  public boolean isParenExpression() {
    return this.kind == ParseTreeKind.PAREN_EXPRESSION;
  }

  public boolean isPostfixExpression() {
    return this.kind == ParseTreeKind.POSTFIX_EXPRESSION;
  }

  public boolean isReturnStatement() {
    return this.kind == ParseTreeKind.RETURN_STATEMENT;
  }

  public boolean isSourceFile() {
    return this.kind == ParseTreeKind.SOURCE_FILE;
  }

  public boolean isSwitchStatement() {
    return this.kind == ParseTreeKind.SWITCH_STATEMENT;
  }

  public boolean isThisExpression() {
    return this.kind == ParseTreeKind.THIS_EXPRESSION;
  }

  public boolean isThrowStatement() {
    return this.kind == ParseTreeKind.THROW_STATEMENT;
  }

  public boolean isTryStatement() {
    return this.kind == ParseTreeKind.TRY_STATEMENT;
  }

  public boolean isUnaryExpression() {
    return this.kind == ParseTreeKind.UNARY_EXPRESSION;
  }

  public boolean isVariableDeclaration() {
    return this.kind == ParseTreeKind.VARIABLE_DECLARATION;
  }

  public boolean isVariableStatement() {
    return this.kind == ParseTreeKind.VARIABLE_STATEMENT;
  }

  public boolean isWhileStatement() {
    return this.kind == ParseTreeKind.WHILE_STATEMENT;
  }

  public boolean isJavascriptArguments() {
    return this.kind == ParseTreeKind.JAVASCRIPT_ARGUMENTS;
  }

  public boolean isJavascriptArrayAccessExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_ARRAY_ACCESS_EXPRESSION;
  }

  public boolean isJavascriptArrayLiteralExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_ARRAY_LITERAL_EXPRESSION;
  }

  public boolean isJavascriptBinaryExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_BINARY_EXPRESSION;
  }

  public boolean isJavascriptBlock() {
    return this.kind == ParseTreeKind.JAVASCRIPT_BLOCK;
  }

  public boolean isJavascriptBreakStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_BREAK_STATEMENT;
  }

  public boolean isJavascriptCallExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_CALL_EXPRESSION;
  }

  public boolean isJavascriptCaseClause() {
    return this.kind == ParseTreeKind.JAVASCRIPT_CASE_CLAUSE;
  }

  public boolean isJavascriptCatchClause() {
    return this.kind == ParseTreeKind.JAVASCRIPT_CATCH_CLAUSE;
  }

  public boolean isJavascriptCommaExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_COMMA_EXPRESSION;
  }

  public boolean isJavascriptConditionalExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_CONDITIONAL_EXPRESSION;
  }

  public boolean isJavascriptContinueStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_CONTINUE_STATEMENT;
  }

  public boolean isJavascriptDebuggerStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_DEBUGGER_STATEMENT;
  }

  public boolean isJavascriptDefaultClause() {
    return this.kind == ParseTreeKind.JAVASCRIPT_DEFAULT_CLAUSE;
  }

  public boolean isJavascriptDoStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_DO_STATEMENT;
  }

  public boolean isJavascriptElision() {
    return this.kind == ParseTreeKind.JAVASCRIPT_ELISION;
  }

  public boolean isJavascriptEmptyStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_EMPTY_STATEMENT;
  }

  public boolean isJavascriptExpressionStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_EXPRESSION_STATEMENT;
  }

  public boolean isJavascriptForInStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_FOR_IN_STATEMENT;
  }

  public boolean isJavascriptForStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_FOR_STATEMENT;
  }

  public boolean isJavascriptFormalParameterList() {
    return this.kind == ParseTreeKind.JAVASCRIPT_FORMAL_PARAMETER_LIST;
  }

  public boolean isJavascriptFunctionExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_FUNCTION_EXPRESSION;
  }

  public boolean isJavascriptGetAccessor() {
    return this.kind == ParseTreeKind.JAVASCRIPT_GET_ACCESSOR;
  }

  public boolean isJavascriptIdentifierExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_IDENTIFIER_EXPRESSION;
  }

  public boolean isJavascriptIfStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_IF_STATEMENT;
  }

  public boolean isJavascriptLabelledStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_LABELLED_STATEMENT;
  }

  public boolean isJavascriptLiteralExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_LITERAL_EXPRESSION;
  }

  public boolean isJavascriptMemberExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_MEMBER_EXPRESSION;
  }

  public boolean isJavascriptNewExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_NEW_EXPRESSION;
  }

  public boolean isJavascriptObjectLiteralExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_OBJECT_LITERAL_EXPRESSION;
  }

  public boolean isJavascriptParenExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_PAREN_EXPRESSION;
  }

  public boolean isJavascriptPostfixExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_POSTFIX_EXPRESSION;
  }

  public boolean isJavascriptProgram() {
    return this.kind == ParseTreeKind.JAVASCRIPT_PROGRAM;
  }

  public boolean isJavascriptPropertyAssignment() {
    return this.kind == ParseTreeKind.JAVASCRIPT_PROPERTY_ASSIGNMENT;
  }

  public boolean isJavascriptReturnStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_RETURN_STATEMENT;
  }

  public boolean isJavascriptSetAccessor() {
    return this.kind == ParseTreeKind.JAVASCRIPT_SET_ACCESSOR;
  }

  public boolean isJavascriptSwitchStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_SWITCH_STATEMENT;
  }

  public boolean isJavascriptThisExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_THIS_EXPRESSION;
  }

  public boolean isJavascriptThrowStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_THROW_STATEMENT;
  }

  public boolean isJavascriptTryStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_TRY_STATEMENT;
  }

  public boolean isJavascriptUnaryExpression() {
    return this.kind == ParseTreeKind.JAVASCRIPT_UNARY_EXPRESSION;
  }

  public boolean isJavascriptVariableDeclaration() {
    return this.kind == ParseTreeKind.JAVASCRIPT_VARIABLE_DECLARATION;
  }

  public boolean isJavascriptVariableStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_VARIABLE_STATEMENT;
  }

  public boolean isJavascriptWhileStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_WHILE_STATEMENT;
  }

  public boolean isJavascriptWithStatement() {
    return this.kind == ParseTreeKind.JAVASCRIPT_WITH_STATEMENT;
  }
}
