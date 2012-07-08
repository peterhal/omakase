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


  public BinaryExpressionTree asBinaryExpression() {
    return (BinaryExpressionTree) this;
  }

  public BlockTree asBlock() {
    return (BlockTree) this;
  }

  public CallExpressionTree asCallExpression() {
    return (CallExpressionTree) this;
  }

  public ClassDeclarationTree asClassDeclaration() {
    return (ClassDeclarationTree) this;
  }

  public ExpressionStatementTree asExpressionStatement() {
    return (ExpressionStatementTree) this;
  }

  public FormalParameterListTree asFormalParameterList() {
    return (FormalParameterListTree) this;
  }

  public FunctionExpressionTree asFunctionExpression() {
    return (FunctionExpressionTree) this;
  }

  public LiteralExpressionTree asLiteralExpression() {
    return (LiteralExpressionTree) this;
  }

  public MethodDeclarationTree asMethodDeclaration() {
    return (MethodDeclarationTree) this;
  }

  public ParameterDeclarationTree asParameterDeclaration() {
    return (ParameterDeclarationTree) this;
  }

  public ParenExpressionTree asParenExpression() {
    return (ParenExpressionTree) this;
  }

  public SimpleNameExpressionTree asSimpleNameExpression() {
    return (SimpleNameExpressionTree) this;
  }

  public SourceFileTree asSourceFile() {
    return (SourceFileTree) this;
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
}
