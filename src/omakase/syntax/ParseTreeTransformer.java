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
 * Base class for transformation compiler passes.
 */
public class ParseTreeTransformer {

  public <T extends ParseTree> ImmutableList<T> transformList(ImmutableList<T> trees) {
    ImmutableList.Builder<T> result = null;
    for (int i = 0; i < trees.size(); i++) {
      ParseTree element = transformAny(trees.get(i));
      if (result == null && element != trees.get(i)) {
        result = new ImmutableList.Builder<T>();
        for (int j = 0; j < i; j++) {
          result.add(trees.get(j));
        }
      }
      if (result != null) {
        result.add((T)element);
      }
    }
    if (result == null) {
      return trees;
    }
    return result.build();
  }

  public ParseTree transformAny(ParseTree tree) {
    if (tree == null) {
      return null;
    }

    switch (tree.kind) {
    case BINARY_EXPRESSION:
      return transform(tree.asBinaryExpression());
    case BLOCK:
      return transform(tree.asBlock());
    case CALL_EXPRESSION:
      return transform(tree.asCallExpression());
    case CLASS_DECLARATION:
      return transform(tree.asClassDeclaration());
    case EXPRESSION_STATEMENT:
      return transform(tree.asExpressionStatement());
    case FORMAL_PARAMETER_LIST:
      return transform(tree.asFormalParameterList());
    case FUNCTION_EXPRESSION:
      return transform(tree.asFunctionExpression());
    case LITERAL_EXPRESSION:
      return transform(tree.asLiteralExpression());
    case METHOD_DECLARATION:
      return transform(tree.asMethodDeclaration());
    case PARAMETER_DECLARATION:
      return transform(tree.asParameterDeclaration());
    case PAREN_EXPRESSION:
      return transform(tree.asParenExpression());
    case SIMPLE_NAME_EXPRESSION:
      return transform(tree.asSimpleNameExpression());
    case SOURCE_FILE:
      return transform(tree.asSourceFile());
    case JAVASCRIPT_ARGUMENTS:
      return transform(tree.asJavascriptArguments());
    case JAVASCRIPT_ARRAY_ACCESS_EXPRESSION:
      return transform(tree.asJavascriptArrayAccessExpression());
    case JAVASCRIPT_ARRAY_LITERAL_EXPRESSION:
      return transform(tree.asJavascriptArrayLiteralExpression());
    case JAVASCRIPT_BINARY_EXPRESSION:
      return transform(tree.asJavascriptBinaryExpression());
    case JAVASCRIPT_BLOCK:
      return transform(tree.asJavascriptBlock());
    case JAVASCRIPT_BREAK_STATEMENT:
      return transform(tree.asJavascriptBreakStatement());
    case JAVASCRIPT_CALL_EXPRESSION:
      return transform(tree.asJavascriptCallExpression());
    case JAVASCRIPT_CASE_CLAUSE:
      return transform(tree.asJavascriptCaseClause());
    case JAVASCRIPT_CATCH_CLAUSE:
      return transform(tree.asJavascriptCatchClause());
    case JAVASCRIPT_COMMA_EXPRESSION:
      return transform(tree.asJavascriptCommaExpression());
    case JAVASCRIPT_CONDITIONAL_EXPRESSION:
      return transform(tree.asJavascriptConditionalExpression());
    case JAVASCRIPT_CONTINUE_STATEMENT:
      return transform(tree.asJavascriptContinueStatement());
    case JAVASCRIPT_DEBUGGER_STATEMENT:
      return transform(tree.asJavascriptDebuggerStatement());
    case JAVASCRIPT_DEFAULT_CLAUSE:
      return transform(tree.asJavascriptDefaultClause());
    case JAVASCRIPT_DO_STATEMENT:
      return transform(tree.asJavascriptDoStatement());
    case JAVASCRIPT_EMPTY_STATEMENT:
      return transform(tree.asJavascriptEmptyStatement());
    case JAVASCRIPT_EXPRESSION_STATEMENT:
      return transform(tree.asJavascriptExpressionStatement());
    case JAVASCRIPT_FOR_IN_STATEMENT:
      return transform(tree.asJavascriptForInStatement());
    case JAVASCRIPT_FOR_STATEMENT:
      return transform(tree.asJavascriptForStatement());
    case JAVASCRIPT_FORMAL_PARAMETER_LIST:
      return transform(tree.asJavascriptFormalParameterList());
    case JAVASCRIPT_FUNCTION_EXPRESSION:
      return transform(tree.asJavascriptFunctionExpression());
    case JAVASCRIPT_GET_ACCESSOR:
      return transform(tree.asJavascriptGetAccessor());
    case JAVASCRIPT_IDENTIFIER_EXPRESSION:
      return transform(tree.asJavascriptIdentifierExpression());
    case JAVASCRIPT_IF_STATEMENT:
      return transform(tree.asJavascriptIfStatement());
    case JAVASCRIPT_LABELLED_STATEMENT:
      return transform(tree.asJavascriptLabelledStatement());
    case JAVASCRIPT_LITERAL_EXPRESSION:
      return transform(tree.asJavascriptLiteralExpression());
    case JAVASCRIPT_MEMBER_EXPRESSION:
      return transform(tree.asJavascriptMemberExpression());
    case JAVASCRIPT_NEW_EXPRESSION:
      return transform(tree.asJavascriptNewExpression());
    case JAVASCRIPT_OBJECT_LITERAL_EXPRESSION:
      return transform(tree.asJavascriptObjectLiteralExpression());
    case JAVASCRIPT_PAREN_EXPRESSION:
      return transform(tree.asJavascriptParenExpression());
    case JAVASCRIPT_POSTFIX_EXPRESSION:
      return transform(tree.asJavascriptPostfixExpression());
    case JAVASCRIPT_PROGRAM:
      return transform(tree.asJavascriptProgram());
    case JAVASCRIPT_PROPERTY_ASSIGNMENT:
      return transform(tree.asJavascriptPropertyAssignment());
    case JAVASCRIPT_RETURN_STATEMENT:
      return transform(tree.asJavascriptReturnStatement());
    case JAVASCRIPT_SET_ACCESSOR:
      return transform(tree.asJavascriptSetAccessor());
    case JAVASCRIPT_SWITCH_STATEMENT:
      return transform(tree.asJavascriptSwitchStatement());
    case JAVASCRIPT_THIS_EXPRESSION:
      return transform(tree.asJavascriptThisExpression());
    case JAVASCRIPT_THROW_STATEMENT:
      return transform(tree.asJavascriptThrowStatement());
    case JAVASCRIPT_TRY_STATEMENT:
      return transform(tree.asJavascriptTryStatement());
    case JAVASCRIPT_UNARY_EXPRESSION:
      return transform(tree.asJavascriptUnaryExpression());
    case JAVASCRIPT_VARIABLE_DECLARATION:
      return transform(tree.asJavascriptVariableDeclaration());
    case JAVASCRIPT_VARIABLE_STATEMENT:
      return transform(tree.asJavascriptVariableStatement());
    case JAVASCRIPT_WHILE_STATEMENT:
      return transform(tree.asJavascriptWhileStatement());
    case JAVASCRIPT_WITH_STATEMENT:
      return transform(tree.asJavascriptWithStatement());
    default:
      throw new RuntimeException("Unexpected tree kind.");
    }
  }

  protected ParseTree transform(BinaryExpressionTree tree) {
    ParseTree left = transformAny(tree.left);
    ParseTree right = transformAny(tree.right);
    if (left == tree.left &&
        right == tree.right) {
      return tree;
    }
    return new BinaryExpressionTree(
        null,
        tree.left,
        tree.operator,
        tree.right);
  }

  protected ParseTree transform(BlockTree tree) {
    ImmutableList<ParseTree> statements = transformList(tree.statements);
    if (statements == tree.statements) {
      return tree;
    }
    return new BlockTree(
        null,
        statements);
  }

  protected ParseTree transform(CallExpressionTree tree) {
    ParseTree function = transformAny(tree.function);
    ImmutableList<ParseTree> arguments = transformList(tree.arguments);
    if (function == tree.function &&
        arguments == tree.arguments) {
      return tree;
    }
    return new CallExpressionTree(
        null,
        tree.function,
        arguments);
  }

  protected ParseTree transform(ClassDeclarationTree tree) {
    ImmutableList<ParseTree> members = transformList(tree.members);
    if (members == tree.members) {
      return tree;
    }
    return new ClassDeclarationTree(
        null,
        tree.name,
        members);
  }

  protected ParseTree transform(ExpressionStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new ExpressionStatementTree(
        null,
        tree.expression);
  }

  protected ParseTree transform(FormalParameterListTree tree) {
    ImmutableList<ParseTree> parameters = transformList(tree.parameters);
    if (parameters == tree.parameters) {
      return tree;
    }
    return new FormalParameterListTree(
        null,
        parameters);
  }

  protected ParseTree transform(FunctionExpressionTree tree) {
    ParseTree parameters = transformAny(tree.parameters);
    ParseTree body = transformAny(tree.body);
    if (parameters == tree.parameters &&
        body == tree.body) {
      return tree;
    }
    return new FunctionExpressionTree(
        null,
        tree.parameters,
        tree.body);
  }

  protected ParseTree transform(LiteralExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(MethodDeclarationTree tree) {
    ImmutableList<ParseTree> formals = transformList(tree.formals);
    ParseTree body = transformAny(tree.body);
    if (formals == tree.formals &&
        body == tree.body) {
      return tree;
    }
    return new MethodDeclarationTree(
        null,
        tree.name,
        formals,
        tree.body);
  }

  protected ParseTree transform(ParameterDeclarationTree tree) {
    return tree;
  }

  protected ParseTree transform(ParenExpressionTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new ParenExpressionTree(
        null,
        tree.expression);
  }

  protected ParseTree transform(SimpleNameExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(SourceFileTree tree) {
    ImmutableList<ParseTree> declarations = transformList(tree.declarations);
    if (declarations == tree.declarations) {
      return tree;
    }
    return new SourceFileTree(
        null,
        declarations);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ArgumentsTree tree) {
    ImmutableList<ParseTree> arguments = transformList(tree.arguments);
    if (arguments == tree.arguments) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ArgumentsTree(
        null,
        arguments);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ArrayAccessExpressionTree tree) {
    ParseTree object = transformAny(tree.object);
    ParseTree member = transformAny(tree.member);
    if (object == tree.object &&
        member == tree.member) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ArrayAccessExpressionTree(
        null,
        tree.object,
        tree.member);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ArrayLiteralExpressionTree tree) {
    ImmutableList<ParseTree> elements = transformList(tree.elements);
    if (elements == tree.elements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ArrayLiteralExpressionTree(
        null,
        elements);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.BinaryExpressionTree tree) {
    ParseTree left = transformAny(tree.left);
    ParseTree right = transformAny(tree.right);
    if (left == tree.left &&
        right == tree.right) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.BinaryExpressionTree(
        null,
        tree.left,
        tree.operator,
        tree.right);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.BlockTree tree) {
    ImmutableList<ParseTree> statements = transformList(tree.statements);
    if (statements == tree.statements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.BlockTree(
        null,
        statements);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.BreakStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.CallExpressionTree tree) {
    ParseTree function = transformAny(tree.function);
    ImmutableList<ParseTree> arguments = transformList(tree.arguments);
    if (function == tree.function &&
        arguments == tree.arguments) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CallExpressionTree(
        null,
        tree.function,
        arguments);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.CaseClauseTree tree) {
    ParseTree expression = transformAny(tree.expression);
    ImmutableList<ParseTree> statements = transformList(tree.statements);
    if (expression == tree.expression &&
        statements == tree.statements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CaseClauseTree(
        null,
        tree.expression,
        statements);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.CatchClauseTree tree) {
    ParseTree block = transformAny(tree.block);
    if (block == tree.block) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CatchClauseTree(
        null,
        tree.identifier,
        tree.block);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.CommaExpressionTree tree) {
    ImmutableList<ParseTree> expressions = transformList(tree.expressions);
    if (expressions == tree.expressions) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CommaExpressionTree(
        null,
        expressions);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ConditionalExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ContinueStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.DebuggerStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.DefaultClauseTree tree) {
    ImmutableList<ParseTree> statements = transformList(tree.statements);
    if (statements == tree.statements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.DefaultClauseTree(
        null,
        statements);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.DoStatementTree tree) {
    ParseTree statement = transformAny(tree.statement);
    ParseTree condition = transformAny(tree.condition);
    if (statement == tree.statement &&
        condition == tree.condition) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.DoStatementTree(
        null,
        tree.statement,
        tree.condition);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.EmptyStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ExpressionStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ExpressionStatementTree(
        null,
        tree.expression);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ForInStatementTree tree) {
    ParseTree element = transformAny(tree.element);
    ParseTree collection = transformAny(tree.collection);
    ParseTree body = transformAny(tree.body);
    if (element == tree.element &&
        collection == tree.collection &&
        body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ForInStatementTree(
        null,
        tree.element,
        tree.collection,
        tree.body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ForStatementTree tree) {
    ParseTree initializer = transformAny(tree.initializer);
    ParseTree condition = transformAny(tree.condition);
    ParseTree increment = transformAny(tree.increment);
    ParseTree body = transformAny(tree.body);
    if (initializer == tree.initializer &&
        condition == tree.condition &&
        increment == tree.increment &&
        body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ForStatementTree(
        null,
        tree.initializer,
        tree.condition,
        tree.increment,
        tree.body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.FormalParameterListTree tree) {
    ImmutableList<ParseTree> parameters = transformList(tree.parameters);
    if (parameters == tree.parameters) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.FormalParameterListTree(
        null,
        parameters);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.FunctionExpressionTree tree) {
    ParseTree parameters = transformAny(tree.parameters);
    ParseTree body = transformAny(tree.body);
    if (parameters == tree.parameters &&
        body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.FunctionExpressionTree(
        null,
        tree.parameters,
        tree.body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.GetAccessorTree tree) {
    ParseTree body = transformAny(tree.body);
    if (body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.GetAccessorTree(
        null,
        tree.propertyName,
        tree.body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.IdentifierExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.IfStatementTree tree) {
    ParseTree condition = transformAny(tree.condition);
    ParseTree ifClause = transformAny(tree.ifClause);
    ParseTree elseClause = transformAny(tree.elseClause);
    if (condition == tree.condition &&
        ifClause == tree.ifClause &&
        elseClause == tree.elseClause) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.IfStatementTree(
        null,
        tree.condition,
        tree.ifClause,
        tree.elseClause);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.LabelledStatementTree tree) {
    ParseTree statement = transformAny(tree.statement);
    if (statement == tree.statement) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.LabelledStatementTree(
        null,
        tree.label,
        tree.statement);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.LiteralExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.MemberExpressionTree tree) {
    ParseTree object = transformAny(tree.object);
    if (object == tree.object) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.MemberExpressionTree(
        null,
        tree.object,
        tree.name);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.NewExpressionTree tree) {
    ParseTree constructor = transformAny(tree.constructor);
    ParseTree arguments = transformAny(tree.arguments);
    if (constructor == tree.constructor &&
        arguments == tree.arguments) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.NewExpressionTree(
        null,
        tree.constructor,
        tree.arguments);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ObjectLiteralExpressionTree tree) {
    ImmutableList<ParseTree> initializers = transformList(tree.initializers);
    if (initializers == tree.initializers) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ObjectLiteralExpressionTree(
        null,
        initializers);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ParenExpressionTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ParenExpressionTree(
        null,
        tree.expression);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.PostfixExpressionTree tree) {
    ParseTree operand = transformAny(tree.operand);
    if (operand == tree.operand) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.PostfixExpressionTree(
        null,
        tree.operand,
        tree.operator);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ProgramTree tree) {
    ImmutableList<ParseTree> sourceElements = transformList(tree.sourceElements);
    if (sourceElements == tree.sourceElements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ProgramTree(
        null,
        sourceElements);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.PropertyAssignmentTree tree) {
    ParseTree value = transformAny(tree.value);
    if (value == tree.value) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.PropertyAssignmentTree(
        null,
        tree.propertyName,
        tree.value);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ReturnStatementTree tree) {
    ParseTree value = transformAny(tree.value);
    if (value == tree.value) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ReturnStatementTree(
        null,
        tree.value);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.SetAccessorTree tree) {
    ParseTree body = transformAny(tree.body);
    if (body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.SetAccessorTree(
        null,
        tree.propertyName,
        tree.parameterName,
        tree.body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.SwitchStatementTree tree) {
    ImmutableList<ParseTree> caseClauses = transformList(tree.caseClauses);
    if (caseClauses == tree.caseClauses) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.SwitchStatementTree(
        null,
        caseClauses);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ThisExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ThrowStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ThrowStatementTree(
        null,
        tree.expression);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.TryStatementTree tree) {
    ParseTree body = transformAny(tree.body);
    ParseTree catchClause = transformAny(tree.catchClause);
    ParseTree finallyClause = transformAny(tree.finallyClause);
    if (body == tree.body &&
        catchClause == tree.catchClause &&
        finallyClause == tree.finallyClause) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.TryStatementTree(
        null,
        tree.body,
        tree.catchClause,
        tree.finallyClause);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.UnaryExpressionTree tree) {
    ParseTree operand = transformAny(tree.operand);
    if (operand == tree.operand) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.UnaryExpressionTree(
        null,
        tree.operator,
        tree.operand);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.VariableDeclarationTree tree) {
    ParseTree initializer = transformAny(tree.initializer);
    if (initializer == tree.initializer) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.VariableDeclarationTree(
        null,
        tree.name,
        tree.initializer);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.VariableStatementTree tree) {
    ImmutableList<ParseTree> declarations = transformList(tree.declarations);
    if (declarations == tree.declarations) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.VariableStatementTree(
        null,
        declarations);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.WhileStatementTree tree) {
    ParseTree condition = transformAny(tree.condition);
    ParseTree body = transformAny(tree.body);
    if (condition == tree.condition &&
        body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.WhileStatementTree(
        null,
        tree.condition,
        tree.body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.WithStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    ParseTree body = transformAny(tree.body);
    if (expression == tree.expression &&
        body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.WithStatementTree(
        null,
        tree.expression,
        tree.body);
  }
}
