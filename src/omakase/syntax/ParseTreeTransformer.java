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
public class ParseTreeTransformer {

  private <T extends ParseTree> ImmutableList<T> transformList(ImmutableList<T> trees) {
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
    case BLOCK:
      return transform(tree.asBlock());
    case CALL_EXPRESSION:
      return transform(tree.asCallExpression());
    case CLASS_DECLARATION:
      return transform(tree.asClassDeclaration());
    case EXPRESSION_STATEMENT:
      return transform(tree.asExpressionStatement());
    case LITERAL_EXPRESSION:
      return transform(tree.asLiteralExpression());
    case METHOD_DECLARATION:
      return transform(tree.asMethodDeclaration());
    case PARAMETER_DECLARATION:
      return transform(tree.asParameterDeclaration());
    case SIMPLE_NAME_EXPRESSION:
      return transform(tree.asSimpleNameExpression());
    case SOURCE_FILE:
      return transform(tree.asSourceFile());
    case JAVASCRIPT_PROGRAM:
      return transform(tree.asJavascriptProgram());
    default:
      throw new RuntimeException("Unexpected tree kind.");
    }
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

  protected ParseTree transform(omakase.syntax.trees.javascript.ProgramTree tree) {
    ImmutableList<ParseTree> sourceElements = transformList(tree.sourceElements);
    if (sourceElements == tree.sourceElements) {
      return tree;
    }
    return new omakase.syntax.trees.javascript.ProgramTree(
        null,
        sourceElements);
  }
}
