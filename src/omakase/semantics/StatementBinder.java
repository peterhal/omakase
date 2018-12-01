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

import omakase.semantics.symbols.LocalVariableSymbol;
import omakase.semantics.types.Type;
import omakase.syntax.ParseTreeVisitor;
import omakase.syntax.trees.*;

import java.util.HashMap;
import java.util.Map;

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
    // Declare all local variables
    Map<String, LocalVariableSymbol> locals = null;
    for (ParseTree statement : tree.statements) {
      if (statement.isVariableStatement()) {
        locals = bindLocalVariableDeclarations(statement.asVariableStatement(), locals);
      }
    }
    StatementBindingContext innerContext = locals == null ? context : context.createLookupContext(new LocalVariableLookupContext(context.lookupContext, locals));
    for (ParseTree statement : tree.statements) {
      bindInnerStatement(innerContext, statement);
    }
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
    String name = tree.identifier.value;
    if (context.containsLocal(name)) {
      reportError(tree, "Duplicate local variable name '%s'.");
    }
    // TODO: What types should be allowed to catch?
    LocalVariableSymbol exceptionVariable = new LocalVariableSymbol(name, tree, context.getTypes().getDynamicType());
    StatementBindingContext innerContext = context
        .createLookupContext(new LocalVariableLookupContext(context.lookupContext, exceptionVariable));
    bindInnerStatement(innerContext, tree.block);
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
    bindInnerStatement(context.createLoopContext(), tree.statement);
    bindBooleanExpression(tree.condition);
  }

  @Override
  protected void visit(EmptyStatementTree tree) {
    // nothing
  }

  @Override
  protected void visit(ExpressionStatementTree tree) {
    // TODO: Check for expressions which do no work.
    new ExpressionBinder(context).bindAndInfer(tree.expression);
  }

  @Override
  protected void visit(ForInStatementTree tree) {
    Type elementType = null;
    if (tree.element.type != null) {
      elementType = bindType(tree.element.type);
    }

    LocalVariableSymbol iterationVariable = declareLocalVariable(tree.element.name.value, elementType, tree.element);
    Type collectionType = bindExpression(tree.collection);
    if (collectionType != null) {
      if (!collectionType.isArrayType()) {
        reportError(tree.collection, "Collection in for-in statement must be an array. Found '%s'", collectionType);
      } else {
        if (elementType == null) {
          iterationVariable.setInferredType(collectionType.asArrayType().elementType);
        } else if (elementType != collectionType.asArrayType().elementType) {
          reportError(tree.collection, "Iteration variable type '%s' not compatible with collection type '%s'.", elementType, collectionType);
        }
      }
    }

    StatementBindingContext innerContext = context
        .createLoopContext()
        .createLookupContext(new LocalVariableLookupContext(context.lookupContext, iterationVariable));
    bindInnerStatement(innerContext, tree.body);
  }

  @Override
  protected void visit(ForStatementTree tree) {
    StatementBindingContext context;
    if (tree.initializer != null && tree.isVariableStatement()) {
      Map<String, LocalVariableSymbol> locals = bindLocalVariableDeclarations(tree.asVariableStatement(), null);
      context = this.context.createLookupContext(new LocalVariableLookupContext(this.context.lookupContext, locals));
    } else {
      context = this.context;
    }

    StatementBinder binder = new StatementBinder(context);
    if (context != this.context) {
      // Binds the variable initializers.
      binder.bind(tree.initializer);
    }
    binder.bindBooleanExpression(tree.initializer);
    binder.bindExpression(tree.increment);
    binder.bind(tree.body);
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
      return;
    } else {
      returnType = context.getReturnType();
    }

    if (tree.value != null) {
      if (returnType != null) {
        bindExpression(tree.value, returnType);
      } else {
        context.addReturn(tree, bindExpression(tree.value));
      }
    } else {
      if (returnType == null) {
        context.addReturn(tree, context.getTypes().getVoidType());
      } else {
        if (returnType != null && !returnType.isVoidType()) {
          reportError(tree, "Must return a value of type '%s'.", returnType);
        }
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
      bindInnerStatement(context.createSwitchContext(expressionType), clause);
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
    visitAny(tree.catchClause);
    if (tree.finallyClause != null) {
      bindInnerStatement(this.context.createFinallyContext(), tree.finallyClause);
    }
  }

  @Override
  protected void visit(VariableDeclarationTree tree) {
    // Most of the heavy lifting has been done in the Block processing.
    LocalVariableSymbol symbol = context.lookupIdentifier(tree.name.value).asLocalVariable();
    if (tree.initializer != null) {
      if (tree.type == null) {
        // inferred type of local
        Type actualType = bindExpression(tree.initializer);
        // TODO: Validate that the type is a valid local variable type.
        symbol.setInferredType(actualType);
      } else {
        bindExpression(tree.initializer, symbol.getType());
      }
    }
  }

  @Override
  protected void visit(WhileStatementTree tree) {
    bindBooleanExpression(tree.condition);
    bindInnerStatement(this.context.createLoopContext(), tree.body);
  }

  @Override
  protected void visit(VariableStatementTree tree) {
    super.visit(tree);
  }

  private Map<String, LocalVariableSymbol> bindLocalVariableDeclarations(VariableStatementTree variableStatement, Map<String, LocalVariableSymbol> locals) {
    for (var childTree : variableStatement.declarations) {
      var variableTree = (VariableDeclarationTree) childTree;
      String name = variableTree.name.value;
      if ((locals != null && locals.containsKey(name)) || context.containsLocal(name)) {
        reportError(variableTree, "Duplicate local variable '%s'.", name);
      }

      Type type = null;
      if (variableTree.type != null) {
        type = bindType(variableTree.type);
      }
      locals = locals == null? new HashMap<String, LocalVariableSymbol>() : locals;
      locals.put(name, new LocalVariableSymbol(name, variableTree, type));
    }
    return locals;
  }

  private LocalVariableSymbol declareLocalVariable(String name, Type type, VariableDeclarationTree variableTree) {
    LocalVariableSymbol iterationVariable = new LocalVariableSymbol(name, variableTree, type);
    if (context.containsLocal(name)) {
      reportError(variableTree, "Duplicate local variable '%s'.", name);
    }
    return iterationVariable;
  }

  private Type bindType(ParseTree type) {
    // TODO: Bind type in class/method/field context.
    return new TypeBinder(context.project).bindType(type);
  }

  private void bindInnerStatement(StatementBindingContext context, ParseTree body) {
    // TODO: Consider setting, resetting context on this.
    new StatementBinder(context).bind(body);
  }

  private void bindExpression(ParseTree expression, Type expectedType) {
    new ExpressionBinder(context).bind(expression, expectedType);
  }

  private Type bindExpression(ParseTree expression) {
    return new ExpressionBinder(context).bindAndInfer(expression);
  }

  private void bindBooleanExpression(ParseTree tree) {
    new ExpressionBinder(context).bindBoolExpression(tree);
  }

  private void reportError(ParseTree tree, String message, Object... args) {
    context.errorReporter().reportError(tree.location.start, message, args);
  }
}
