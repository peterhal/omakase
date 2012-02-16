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
  public void visitAny(ParseTree tree) {
    if (tree == null) {
      return;
    }

    switch (tree.kind) {
    case CLASS_DECLARATION:
      visit(tree.asClassDeclaration());
      break;
    case METHOD_DECLARATION:
      visit(tree.asMethodDeclaration());
      break;
    case PARAMETER_DECLARATION:
      visit(tree.asParameterDeclaration());
      break;
    case BLOCK:
      visit(tree.asBlock());
      break;
    case EXPRESSION_STATEMENT:
      visit(tree.asExpressionStatement());
      break;
    case LITERAL_EXPRESSION:
      visit(tree.asLiteralExpression());
      break;
    case SIMPLE_NAME_EXPRESSION:
      visit(tree.asSimpleNameExpression());
      break;
    case CALL_EXPRESSION:
      visit(tree.asCallExpression());
      break;
    case SOURCE_FILE:
      visit(tree.asSourceFile());
      break;
    }
  }

  protected void visit(SourceFileTree tree) {
    visitList(tree.declarations);
  }

  protected void visit(CallExpressionTree tree) {
    visitAny(tree.function);
    visitList(tree.arguments);
  }

  protected void visit(SimpleNameExpressionTree tree) {

  }

  protected void visit(LiteralExpressionTree tree) {

  }

  protected void visit(ExpressionStatementTree tree) {
    visitAny(tree.expression);
  }

  protected void visit(BlockTree tree) {
    visitList(tree.statements);
  }

  protected void visit(ParameterDeclarationTree tree) {

  }

  protected void visit(MethodDeclarationTree tree) {
    visitList(tree.formals);
    visitAny(tree.body);
  }

  protected void visitList(ImmutableList<ParseTree> trees) {
    for (ParseTree tree: trees) {
      visitAny(tree);
    }
  }

  protected void visit(ClassDeclarationTree tree) {
    visitList(tree.members);
  }
}
