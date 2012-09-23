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

  @SuppressWarnings("unchecked")
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
    case ARGUMENTS:
      return transform(tree.asArguments());
    case ARRAY_ACCESS_EXPRESSION:
      return transform(tree.asArrayAccessExpression());
    case ARRAY_LITERAL_EXPRESSION:
      return transform(tree.asArrayLiteralExpression());
    case ARRAY_TYPE:
      return transform(tree.asArrayType());
    case BINARY_EXPRESSION:
      return transform(tree.asBinaryExpression());
    case BLOCK:
      return transform(tree.asBlock());
    case BREAK_STATEMENT:
      return transform(tree.asBreakStatement());
    case CALL_EXPRESSION:
      return transform(tree.asCallExpression());
    case CASE_CLAUSE:
      return transform(tree.asCaseClause());
    case CATCH_CLAUSE:
      return transform(tree.asCatchClause());
    case CLASS_DECLARATION:
      return transform(tree.asClassDeclaration());
    case CONDITIONAL_EXPRESSION:
      return transform(tree.asConditionalExpression());
    case CONTINUE_STATEMENT:
      return transform(tree.asContinueStatement());
    case DEBUGGER_STATEMENT:
      return transform(tree.asDebuggerStatement());
    case DEFAULT_CLAUSE:
      return transform(tree.asDefaultClause());
    case DO_STATEMENT:
      return transform(tree.asDoStatement());
    case EMPTY_STATEMENT:
      return transform(tree.asEmptyStatement());
    case EXPRESSION_STATEMENT:
      return transform(tree.asExpressionStatement());
    case FIELD_DECLARATION:
      return transform(tree.asFieldDeclaration());
    case FOR_IN_STATEMENT:
      return transform(tree.asForInStatement());
    case FOR_STATEMENT:
      return transform(tree.asForStatement());
    case FORMAL_PARAMETER_LIST:
      return transform(tree.asFormalParameterList());
    case FUNCTION_EXPRESSION:
      return transform(tree.asFunctionExpression());
    case FUNCTION_TYPE:
      return transform(tree.asFunctionType());
    case IDENTIFIER_EXPRESSION:
      return transform(tree.asIdentifierExpression());
    case IF_STATEMENT:
      return transform(tree.asIfStatement());
    case KEYWORD_TYPE:
      return transform(tree.asKeywordType());
    case LITERAL_EXPRESSION:
      return transform(tree.asLiteralExpression());
    case MEMBER_EXPRESSION:
      return transform(tree.asMemberExpression());
    case METHOD_DECLARATION:
      return transform(tree.asMethodDeclaration());
    case NAMED_TYPE:
      return transform(tree.asNamedType());
    case NEW_EXPRESSION:
      return transform(tree.asNewExpression());
    case NULLABLE_TYPE:
      return transform(tree.asNullableType());
    case PARAMETER_DECLARATION:
      return transform(tree.asParameterDeclaration());
    case PAREN_EXPRESSION:
      return transform(tree.asParenExpression());
    case POSTFIX_EXPRESSION:
      return transform(tree.asPostfixExpression());
    case RETURN_STATEMENT:
      return transform(tree.asReturnStatement());
    case SOURCE_FILE:
      return transform(tree.asSourceFile());
    case SWITCH_STATEMENT:
      return transform(tree.asSwitchStatement());
    case THIS_EXPRESSION:
      return transform(tree.asThisExpression());
    case THROW_STATEMENT:
      return transform(tree.asThrowStatement());
    case TRY_STATEMENT:
      return transform(tree.asTryStatement());
    case TYPE_ARGUMENT_LIST:
      return transform(tree.asTypeArgumentList());
    case UNARY_EXPRESSION:
      return transform(tree.asUnaryExpression());
    case VARIABLE_DECLARATION:
      return transform(tree.asVariableDeclaration());
    case VARIABLE_STATEMENT:
      return transform(tree.asVariableStatement());
    case WHILE_STATEMENT:
      return transform(tree.asWhileStatement());
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
    case JAVASCRIPT_ELISION:
      return transform(tree.asJavascriptElision());
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

  protected ParseTree transform(ArgumentsTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> arguments = transformList(tree.arguments);
    if (arguments == tree.arguments) {
      return tree;
    }
    return new ArgumentsTree(
        null,
        arguments);
  }

  protected ParseTree transform(ArrayAccessExpressionTree tree) {
    ParseTree object = transformAny(tree.object);
    ParseTree member = transformAny(tree.member);
    if (object == tree.object &&
        member == tree.member) {
      return tree;
    }
    return new ArrayAccessExpressionTree(
        null,
        object,
        member);
  }

  protected ParseTree transform(ArrayLiteralExpressionTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> elements = transformList(tree.elements);
    if (elements == tree.elements) {
      return tree;
    }
    return new ArrayLiteralExpressionTree(
        null,
        elements);
  }

  protected ParseTree transform(ArrayTypeTree tree) {
    ParseTree elementType = transformAny(tree.elementType);
    if (elementType == tree.elementType) {
      return tree;
    }
    return new ArrayTypeTree(
        null,
        elementType);
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
        left,
        tree.operator,
        right);
  }

  protected ParseTree transform(BlockTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> statements = transformList(tree.statements);
    if (statements == tree.statements) {
      return tree;
    }
    return new BlockTree(
        null,
        statements);
  }

  protected ParseTree transform(BreakStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(CallExpressionTree tree) {
    ParseTree function = transformAny(tree.function);
    ParseTree arguments = transformAny(tree.arguments);
    if (function == tree.function &&
        arguments == tree.arguments) {
      return tree;
    }
    return new CallExpressionTree(
        null,
        function,
        arguments.asArguments());
  }

  protected ParseTree transform(CaseClauseTree tree) {
    ParseTree expression = transformAny(tree.expression);
    ImmutableList<? extends omakase.syntax.trees.ParseTree> statements = transformList(tree.statements);
    if (expression == tree.expression &&
        statements == tree.statements) {
      return tree;
    }
    return new CaseClauseTree(
        null,
        expression,
        statements);
  }

  protected ParseTree transform(CatchClauseTree tree) {
    ParseTree block = transformAny(tree.block);
    if (block == tree.block) {
      return tree;
    }
    return new CatchClauseTree(
        null,
        tree.identifier,
        block.asBlock());
  }

  protected ParseTree transform(ClassDeclarationTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> members = transformList(tree.members);
    if (members == tree.members) {
      return tree;
    }
    return new ClassDeclarationTree(
        null,
        tree.isExtern,
        tree.name,
        members);
  }

  protected ParseTree transform(ConditionalExpressionTree tree) {
    ParseTree condition = transformAny(tree.condition);
    ParseTree left = transformAny(tree.left);
    ParseTree right = transformAny(tree.right);
    if (condition == tree.condition &&
        left == tree.left &&
        right == tree.right) {
      return tree;
    }
    return new ConditionalExpressionTree(
        null,
        condition,
        left,
        right);
  }

  protected ParseTree transform(ContinueStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(DebuggerStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(DefaultClauseTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> statements = transformList(tree.statements);
    if (statements == tree.statements) {
      return tree;
    }
    return new DefaultClauseTree(
        null,
        statements);
  }

  protected ParseTree transform(DoStatementTree tree) {
    ParseTree statement = transformAny(tree.statement);
    ParseTree condition = transformAny(tree.condition);
    if (statement == tree.statement &&
        condition == tree.condition) {
      return tree;
    }
    return new DoStatementTree(
        null,
        statement,
        condition);
  }

  protected ParseTree transform(EmptyStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(ExpressionStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new ExpressionStatementTree(
        null,
        expression);
  }

  protected ParseTree transform(FieldDeclarationTree tree) {
    ImmutableList<? extends omakase.syntax.trees.VariableDeclarationTree> declarations = transformList(tree.declarations);
    if (declarations == tree.declarations) {
      return tree;
    }
    return new FieldDeclarationTree(
        null,
        tree.isStatic,
        declarations);
  }

  protected ParseTree transform(ForInStatementTree tree) {
    ParseTree element = transformAny(tree.element);
    ParseTree collection = transformAny(tree.collection);
    ParseTree body = transformAny(tree.body);
    if (element == tree.element &&
        collection == tree.collection &&
        body == tree.body) {
      return tree;
    }
    return new ForInStatementTree(
        null,
        element,
        collection,
        body);
  }

  protected ParseTree transform(ForStatementTree tree) {
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
    return new ForStatementTree(
        null,
        initializer,
        condition,
        increment,
        body);
  }

  protected ParseTree transform(FormalParameterListTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParameterDeclarationTree> parameters = transformList(tree.parameters);
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
        parameters.asFormalParameterList(),
        body);
  }

  protected ParseTree transform(FunctionTypeTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> argumentTypes = transformList(tree.argumentTypes);
    ParseTree returnType = transformAny(tree.returnType);
    if (argumentTypes == tree.argumentTypes &&
        returnType == tree.returnType) {
      return tree;
    }
    return new FunctionTypeTree(
        null,
        argumentTypes,
        returnType);
  }

  protected ParseTree transform(IdentifierExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(IfStatementTree tree) {
    ParseTree condition = transformAny(tree.condition);
    ParseTree ifClause = transformAny(tree.ifClause);
    ParseTree elseClause = transformAny(tree.elseClause);
    if (condition == tree.condition &&
        ifClause == tree.ifClause &&
        elseClause == tree.elseClause) {
      return tree;
    }
    return new IfStatementTree(
        null,
        condition,
        ifClause,
        elseClause);
  }

  protected ParseTree transform(KeywordTypeTree tree) {
    return tree;
  }

  protected ParseTree transform(LiteralExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(MemberExpressionTree tree) {
    ParseTree object = transformAny(tree.object);
    if (object == tree.object) {
      return tree;
    }
    return new MemberExpressionTree(
        null,
        object,
        tree.name);
  }

  protected ParseTree transform(MethodDeclarationTree tree) {
    ParseTree returnType = transformAny(tree.returnType);
    ParseTree formals = transformAny(tree.formals);
    ParseTree body = transformAny(tree.body);
    if (returnType == tree.returnType &&
        formals == tree.formals &&
        body == tree.body) {
      return tree;
    }
    return new MethodDeclarationTree(
        null,
        returnType,
        tree.name,
        formals.asFormalParameterList(),
        tree.isStatic,
        tree.isNative,
        body);
  }

  protected ParseTree transform(NamedTypeTree tree) {
    ParseTree element = transformAny(tree.element);
    ParseTree typeArguments = transformAny(tree.typeArguments);
    if (element == tree.element &&
        typeArguments == tree.typeArguments) {
      return tree;
    }
    return new NamedTypeTree(
        null,
        element.asNamedType(),
        tree.name,
        typeArguments.asTypeArgumentList());
  }

  protected ParseTree transform(NewExpressionTree tree) {
    ParseTree constructor = transformAny(tree.constructor);
    ParseTree arguments = transformAny(tree.arguments);
    if (constructor == tree.constructor &&
        arguments == tree.arguments) {
      return tree;
    }
    return new NewExpressionTree(
        null,
        constructor,
        arguments.asArguments());
  }

  protected ParseTree transform(NullableTypeTree tree) {
    ParseTree elementType = transformAny(tree.elementType);
    if (elementType == tree.elementType) {
      return tree;
    }
    return new NullableTypeTree(
        null,
        elementType);
  }

  protected ParseTree transform(ParameterDeclarationTree tree) {
    ParseTree type = transformAny(tree.type);
    if (type == tree.type) {
      return tree;
    }
    return new ParameterDeclarationTree(
        null,
        type,
        tree.name);
  }

  protected ParseTree transform(ParenExpressionTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new ParenExpressionTree(
        null,
        expression);
  }

  protected ParseTree transform(PostfixExpressionTree tree) {
    ParseTree operand = transformAny(tree.operand);
    if (operand == tree.operand) {
      return tree;
    }
    return new PostfixExpressionTree(
        null,
        operand,
        tree.operator);
  }

  protected ParseTree transform(ReturnStatementTree tree) {
    ParseTree value = transformAny(tree.value);
    if (value == tree.value) {
      return tree;
    }
    return new ReturnStatementTree(
        null,
        value);
  }

  protected ParseTree transform(SourceFileTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> declarations = transformList(tree.declarations);
    if (declarations == tree.declarations) {
      return tree;
    }
    return new SourceFileTree(
        null,
        declarations);
  }

  protected ParseTree transform(SwitchStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    ImmutableList<? extends omakase.syntax.trees.ParseTree> caseClauses = transformList(tree.caseClauses);
    if (expression == tree.expression &&
        caseClauses == tree.caseClauses) {
      return tree;
    }
    return new SwitchStatementTree(
        null,
        expression,
        caseClauses);
  }

  protected ParseTree transform(ThisExpressionTree tree) {
    return tree;
  }

  protected ParseTree transform(ThrowStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new ThrowStatementTree(
        null,
        expression);
  }

  protected ParseTree transform(TryStatementTree tree) {
    ParseTree body = transformAny(tree.body);
    ParseTree catchClause = transformAny(tree.catchClause);
    ParseTree finallyClause = transformAny(tree.finallyClause);
    if (body == tree.body &&
        catchClause == tree.catchClause &&
        finallyClause == tree.finallyClause) {
      return tree;
    }
    return new TryStatementTree(
        null,
        body.asBlock(),
        catchClause.asCatchClause(),
        finallyClause.asBlock());
  }

  protected ParseTree transform(TypeArgumentListTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> typeArguments = transformList(tree.typeArguments);
    if (typeArguments == tree.typeArguments) {
      return tree;
    }
    return new TypeArgumentListTree(
        null,
        typeArguments);
  }

  protected ParseTree transform(UnaryExpressionTree tree) {
    ParseTree operand = transformAny(tree.operand);
    if (operand == tree.operand) {
      return tree;
    }
    return new UnaryExpressionTree(
        null,
        tree.operator,
        operand);
  }

  protected ParseTree transform(VariableDeclarationTree tree) {
    ParseTree type = transformAny(tree.type);
    ParseTree initializer = transformAny(tree.initializer);
    if (type == tree.type &&
        initializer == tree.initializer) {
      return tree;
    }
    return new VariableDeclarationTree(
        null,
        tree.name,
        type,
        initializer);
  }

  protected ParseTree transform(VariableStatementTree tree) {
    ImmutableList<? extends omakase.syntax.trees.VariableDeclarationTree> declarations = transformList(tree.declarations);
    if (declarations == tree.declarations) {
      return tree;
    }
    return new VariableStatementTree(
        null,
        declarations);
  }

  protected ParseTree transform(WhileStatementTree tree) {
    ParseTree condition = transformAny(tree.condition);
    ParseTree body = transformAny(tree.body);
    if (condition == tree.condition &&
        body == tree.body) {
      return tree;
    }
    return new WhileStatementTree(
        null,
        condition,
        body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ArgumentsTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> arguments = transformList(tree.arguments);
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
        object,
        member);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ArrayLiteralExpressionTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> elements = transformList(tree.elements);
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
        left,
        tree.operator,
        right);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.BlockTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> statements = transformList(tree.statements);
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
    ParseTree arguments = transformAny(tree.arguments);
    if (function == tree.function &&
        arguments == tree.arguments) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CallExpressionTree(
        null,
        function,
        arguments.asJavascriptArguments());
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.CaseClauseTree tree) {
    ParseTree expression = transformAny(tree.expression);
    ImmutableList<? extends omakase.syntax.trees.ParseTree> statements = transformList(tree.statements);
    if (expression == tree.expression &&
        statements == tree.statements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CaseClauseTree(
        null,
        expression,
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
        block.asJavascriptBlock());
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.CommaExpressionTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> expressions = transformList(tree.expressions);
    if (expressions == tree.expressions) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.CommaExpressionTree(
        null,
        expressions);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ConditionalExpressionTree tree) {
    ParseTree condition = transformAny(tree.condition);
    ParseTree left = transformAny(tree.left);
    ParseTree right = transformAny(tree.right);
    if (condition == tree.condition &&
        left == tree.left &&
        right == tree.right) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ConditionalExpressionTree(
        null,
        condition,
        left,
        right);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ContinueStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.DebuggerStatementTree tree) {
    return tree;
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.DefaultClauseTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> statements = transformList(tree.statements);
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
        statement,
        condition);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ElisionTree tree) {
    return tree;
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
        expression);
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
        element,
        collection,
        body);
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
        initializer,
        condition,
        increment,
        body);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.FormalParameterListTree tree) {
    return tree;
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
        tree.name,
        parameters.asJavascriptFormalParameterList(),
        body.asJavascriptBlock());
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.GetAccessorTree tree) {
    ParseTree body = transformAny(tree.body);
    if (body == tree.body) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.GetAccessorTree(
        null,
        tree.propertyName,
        body.asJavascriptBlock());
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
        condition,
        ifClause,
        elseClause);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.LabelledStatementTree tree) {
    ParseTree statement = transformAny(tree.statement);
    if (statement == tree.statement) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.LabelledStatementTree(
        null,
        tree.label,
        statement);
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
        object,
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
        constructor,
        arguments.asJavascriptArguments());
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ObjectLiteralExpressionTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> initializers = transformList(tree.initializers);
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
        expression);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.PostfixExpressionTree tree) {
    ParseTree operand = transformAny(tree.operand);
    if (operand == tree.operand) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.PostfixExpressionTree(
        null,
        operand,
        tree.operator);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ProgramTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> sourceElements = transformList(tree.sourceElements);
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
        value);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.ReturnStatementTree tree) {
    ParseTree value = transformAny(tree.value);
    if (value == tree.value) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ReturnStatementTree(
        null,
        value);
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
        body.asJavascriptBlock());
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.SwitchStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    ImmutableList<? extends omakase.syntax.trees.ParseTree> caseClauses = transformList(tree.caseClauses);
    if (expression == tree.expression &&
        caseClauses == tree.caseClauses) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.SwitchStatementTree(
        null,
        expression,
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
        expression);
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
        body.asJavascriptBlock(),
        catchClause.asJavascriptCatchClause(),
        finallyClause.asJavascriptBlock());
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.UnaryExpressionTree tree) {
    ParseTree operand = transformAny(tree.operand);
    if (operand == tree.operand) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.UnaryExpressionTree(
        null,
        tree.operator,
        operand);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.VariableDeclarationTree tree) {
    ParseTree initializer = transformAny(tree.initializer);
    if (initializer == tree.initializer) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.VariableDeclarationTree(
        null,
        tree.name,
        initializer);
  }

  protected ParseTree transform(omakase.syntax.trees.javascript.VariableStatementTree tree) {
    ImmutableList<? extends omakase.syntax.trees.ParseTree> declarations = transformList(tree.declarations);
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
        condition,
        body);
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
        expression,
        body);
  }
}
