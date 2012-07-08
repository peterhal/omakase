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
import omakase.syntax.trees.javascript.IdentifierExpressionTree;

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
    case JAVASCRIPT_BINARY_EXPRESSION:
      return transform(tree.asJavascriptBinaryExpression());
    case JAVASCRIPT_BLOCK:
      return transform(tree.asJavascriptBlock());
    case JAVASCRIPT_CALL_EXPRESSION:
      return transform(tree.asJavascriptCallExpression());
    case JAVASCRIPT_EXPRESSION_STATEMENT:
      return transform(tree.asJavascriptExpressionStatement());
    case JAVASCRIPT_FORMAL_PARAMETER_LIST:
      return transform(tree.asJavascriptFormalParameterList());
    case JAVASCRIPT_FUNCTION_EXPRESSION:
      return transform(tree.asJavascriptFunctionExpression());
    case JAVASCRIPT_PAREN_EXPRESSION:
      return transform(tree.asJavascriptParenExpression());
    case JAVASCRIPT_PROGRAM:
      return transform(tree.asJavascriptProgram());
    case JAVASCRIPT_IDENTIFIER_EXPRESSION:
      return transform(tree.asJavascriptSimpleNameExpression());
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

  protected ParseTree transform(omakase.syntax.trees.javascript.ExpressionStatementTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ExpressionStatementTree(
        null,
        tree.expression);
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

  protected ParseTree transform(omakase.syntax.trees.javascript.ParenExpressionTree tree) {
    ParseTree expression = transformAny(tree.expression);
    if (expression == tree.expression) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ParenExpressionTree(
        null,
        tree.expression);
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

  protected ParseTree transform(IdentifierExpressionTree tree) {
    return tree;
  }
}
