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

package omakase.semantics;

import omakase.syntax.ParseTreeVisitor;
import omakase.syntax.trees.*;

/**
 * Binds all expressions in statements.
 * NOTE: Does not do flow control.
 */
public class StatementBinder extends ParseTreeVisitor {
  private final StatementBindingContext context;

  public StatementBinder(StatementBindingContext context) {
    this.context = context;
  }

  public void bind(ParseTree tree) {
    this.visitAny(tree);
  }

  @Override
  protected void visit(BlockTree tree) {
    super.visit(tree);
  }

  @Override
  protected void visit(BreakStatementTree tree) {
    if (!context.hasBreakLabel()) {
      reportError(tree, "'break' statement must be contained within for, while, do or switch statement.");
    }
  }

  @Override
  protected void visit(CaseClauseTree tree) {
    bindExpression(tree.expression, context.getSwitchExpressionType());
    // TODO: Check for duplicate case clauses.
    // TODO: Check for constant case clauses.
    for (ParseTree statement : tree.statements) {
      bind(statement);
    }
  }

  @Override
  protected void visit(CatchClauseTree tree) {
    // TODO
  }

  @Override
  protected void visit(ContinueStatementTree tree) {
    if (!context.hasContinueLabel()) {
      reportError(tree, "'continue' statement must be contained within for, while, do or switch statement.");
    }
  }

  @Override
  protected void visit(DebuggerStatementTree tree) {
    // nothing
  }

  @Override
  protected void visit(DefaultClauseTree tree) {
    // just bind the statement list.
    super.visit(tree);
  }

  @Override
  protected void visit(DoStatementTree tree) {
    bindInnerStatement(new LoopStatementContext(context), tree.statement);
    bindBooleanExpression(tree.condition);
  }

  @Override
  protected void visit(EmptyStatementTree tree) {
    // nothing
  }

  @Override
  protected void visit(ExpressionStatementTree tree) {
    new ExpressionBinder(context).bind(tree.expression);
  }

  @Override
  protected void visit(ForInStatementTree tree) {
    // TODO
  }

  @Override
  protected void visit(ForStatementTree tree) {
    // TODO
  }

  @Override
  protected void visit(IfStatementTree tree) {
    bindBooleanExpression(tree.condition);
    bind(tree.ifClause);
    bind(tree.elseClause);
  }

  @Override
  protected void visit(ReturnStatementTree tree) {
    Type returnType = null;
    if (!context.canReturn()) {
      reportError(tree, "Cannot return from within a finally block.");
    } else {
      returnType = context.getReturnType();
    }

    if (tree.value != null) {
      bindExpression(tree.value, returnType);
    } else {
      if (returnType != null && !returnType.isVoidType()) {
        reportError(tree, "Must return a value of type '%s'.", returnType);
      }
    }
  }

  @Override
  protected void visit(SwitchStatementTree tree) {

    Type expressionType = bindExpression(tree.expression);
    if (expressionType != null && !(expressionType.isNumberType() || expressionType.isStringType())) {
      reportError(tree.expression, "Switch expression must be string or number. Found '%s'.", expressionType);
    }
    for (ParseTree clause : tree.caseClauses) {
      bindInnerStatement(new SwitchStatementContext(context, expressionType), clause);
    }
  }

  @Override
  protected void visit(ThrowStatementTree tree) {
    Type exceptionType = bindExpression(tree.expression);
    // TODO: Ensure thrown type is valid?
  }

  @Override
  protected void visit(TryStatementTree tree) {
    bind(tree.body);
    visit(tree.catchClause);
    if (tree.finallyClause != null) {
      bindInnerStatement(new FinallyContext(this.context), tree.finallyClause);
    }
  }

  @Override
  protected void visit(VariableDeclarationTree tree) {
    // TODO
    super.visit(tree);
  }

  @Override
  protected void visit(WhileStatementTree tree) {
    bindBooleanExpression(tree.condition);
    bindInnerStatement(new LoopStatementContext(this.context), tree.body);
  }

  @Override
  protected void visit(VariableStatementTree tree) {
    super.visit(tree);
  }

  private void bindInnerStatement(StatementBindingContext context, ParseTree body) {
    // TODO: Consider setting, resetting context on this.
    new StatementBinder(context).bind(body);
  }

  private void bindExpression(ParseTree expression, Type expectedType) {
    new ExpressionBinder(context).bind(expression, expectedType);
  }

  private Type bindExpression(ParseTree expression) {
    new ExpressionBinder(context).bind(expression);
    return context.getResults().getType(expression);
  }

  private void bindBooleanExpression(ParseTree tree) {
    new ExpressionBinder(context).bindBooleanExpression(tree);
  }

  private void reportError(ParseTree tree, String message, Object... args) {
    context.errorReporter().reportError(tree.location.start, message, args);
  }
}
