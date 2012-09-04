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

package omakase.codegeneration;

import com.google.common.collect.ImmutableList;
import omakase.syntax.ParseTreeTransformer;
import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.tokens.javascript.IdentifierToken;
import omakase.syntax.tokens.javascript.NumericLiteralToken;
import omakase.syntax.tokens.javascript.StringLiteralToken;
import omakase.syntax.trees.*;
import omakase.syntax.trees.ArgumentsTree;
import omakase.syntax.trees.ArrayAccessExpressionTree;
import omakase.syntax.trees.ArrayLiteralExpressionTree;
import omakase.syntax.trees.BinaryExpressionTree;
import omakase.syntax.trees.BlockTree;
import omakase.syntax.trees.BreakStatementTree;
import omakase.syntax.trees.CallExpressionTree;
import omakase.syntax.trees.CaseClauseTree;
import omakase.syntax.trees.CatchClauseTree;
import omakase.syntax.trees.ConditionalExpressionTree;
import omakase.syntax.trees.ContinueStatementTree;
import omakase.syntax.trees.DebuggerStatementTree;
import omakase.syntax.trees.DefaultClauseTree;
import omakase.syntax.trees.DoStatementTree;
import omakase.syntax.trees.EmptyStatementTree;
import omakase.syntax.trees.ExpressionStatementTree;
import omakase.syntax.trees.ForInStatementTree;
import omakase.syntax.trees.ForStatementTree;
import omakase.syntax.trees.FormalParameterListTree;
import omakase.syntax.trees.FunctionExpressionTree;
import omakase.syntax.trees.IdentifierExpressionTree;
import omakase.syntax.trees.IfStatementTree;
import omakase.syntax.trees.LiteralExpressionTree;
import omakase.syntax.trees.NewExpressionTree;
import omakase.syntax.trees.ParenExpressionTree;
import omakase.syntax.trees.PostfixExpressionTree;
import omakase.syntax.trees.ReturnStatementTree;
import omakase.syntax.trees.SwitchStatementTree;
import omakase.syntax.trees.ThisExpressionTree;
import omakase.syntax.trees.ThrowStatementTree;
import omakase.syntax.trees.TryStatementTree;
import omakase.syntax.trees.UnaryExpressionTree;
import omakase.syntax.trees.VariableDeclarationTree;
import omakase.syntax.trees.VariableStatementTree;
import omakase.syntax.trees.WhileStatementTree;
import omakase.syntax.trees.javascript.*;

import java.util.ArrayList;
import java.util.List;

import static omakase.codegeneration.JavascriptParseTreeFactory.*;

/**
 *
 */
public class JavascriptTransformer extends ParseTreeTransformer {
  @Override
  protected ParseTree transform(SourceFileTree tree) {
    return new omakase.syntax.trees.javascript.ProgramTree(null, transformList(tree.declarations));
  }

  @Override
  protected omakase.syntax.trees.javascript.ArgumentsTree transform(ArgumentsTree tree) {
    return createArguments(transformList(tree.arguments));
  }

  @Override
  protected ParseTree transform(ArrayAccessExpressionTree tree) {
    return createArrayAccess(transformAny(tree.object), transformAny(tree.member));
  }

  @Override
  protected ParseTree transform(ArrayLiteralExpressionTree tree) {
    return createArrayLiteral(transformList(tree.elements));
  }

  @Override
  protected ParseTree transform(BinaryExpressionTree tree) {
    return createBinaryExpression(transformAny(tree.left), transformOperatorToken(tree.operator), transformAny(tree.right));
  }

  @Override
  protected omakase.syntax.trees.javascript.BlockTree transform(BlockTree tree) {
    return createBlock(transformList(tree.statements));
  }

  @Override
  protected ParseTree transform(BreakStatementTree tree) {
    return createBreak();
  }

  @Override
  protected ParseTree transform(CallExpressionTree tree) {
    return createCall(transformAny(tree.function), transform(tree.arguments).asJavascriptArguments());
  }

  @Override
  protected ParseTree transform(CaseClauseTree tree) {
    return createCaseClause(transformAny(tree.expression), transformList(tree.statements));
  }

  @Override
  protected omakase.syntax.trees.javascript.CatchClauseTree transform(CatchClauseTree tree) {
    return createCatchClause(createIdentifierToken(tree.identifier.value), transformAny(tree.block).asJavascriptBlock());
  }

  @Override
  protected ParseTree transform(ConditionalExpressionTree tree) {
    return createConditional(transformAny(tree.condition), transformAny(tree.left), transformAny(tree.right));
  }

  @Override
  protected ParseTree transform(ContinueStatementTree tree) {
    return createContinue();
  }

  @Override
  protected ParseTree transform(DebuggerStatementTree tree) {
    return createDebugger();
  }

  @Override
  protected ParseTree transform(DefaultClauseTree tree) {
    return createDefaultClause(transformList(tree.statements));
  }

  @Override
  protected ParseTree transform(DoStatementTree tree) {
    return createDoStatement(transformAny(tree.statement), transformAny(tree.condition));
  }

  @Override
  protected ParseTree transform(EmptyStatementTree tree) {
    return createEmptyStatement();
  }

  @Override
  protected ParseTree transform(ExpressionStatementTree tree) {
    return createExpressionStatement(transformAny(tree.expression));
  }

  @Override
  protected ParseTree transform(ForInStatementTree tree) {
    return createForInStatement(transformAny(tree.element), transformAny(tree.collection), transformAny(tree.body));
  }

  @Override
  protected ParseTree transform(ForStatementTree tree) {
    return createForStatement(transformAny(tree.initializer), transformAny(tree.condition), transformAny(tree.increment), transformAny(tree.body));
  }

  @Override
  protected omakase.syntax.trees.javascript.FormalParameterListTree transform(FormalParameterListTree tree) {
    return createFormalParameterList(transformFormalParameterList(tree.parameters));
  }

  private ImmutableList<omakase.syntax.tokens.javascript.IdentifierToken> transformFormalParameterList(ImmutableList<ParseTree> parameters) {
    ImmutableList.Builder<IdentifierToken> names = new ImmutableList.Builder<IdentifierToken>();
    for (ParseTree parameter : parameters) {
      names.add(createIdentifierToken(parameter.asParameterDeclaration().name.value));
    }
    return names.build();
  }

  @Override
  protected ParseTree transform(FunctionExpressionTree tree) {
    omakase.syntax.trees.javascript.FormalParameterListTree parameters = transform(tree.parameters);
    omakase.syntax.trees.javascript.BlockTree body;
    if (tree.body.isBlock()) {
      body = transform(tree.body.asBlock());
    } else {
      body = createBlock(createReturnStatement(transformAny(tree.body)));
    }
    // TODO: Only do this binding if this is used in body.
    return createThisBoundFunction(createFunction(parameters, body));
  }

  @Override
  protected ParseTree transform(IdentifierExpressionTree tree) {
    return createIdentifier(tree.name.value);
  }

  @Override
  protected ParseTree transform(IfStatementTree tree) {
    return createIfStatement(transformAny(tree.condition), transformAny(tree.ifClause), transformAny(tree.elseClause));
  }

  @Override
  protected ParseTree transform(LiteralExpressionTree tree) {
    return createLiteral(transformLiteral(tree.value));
  }

  private Token transformLiteral(Token value) {
    switch (value.kind) {
    case FALSE:
      return new Token(TokenKind.JS_FALSE, null);
    case NUMBER:
      return new NumericLiteralToken(null, value.asNumericLiteral().value);
    case STRING:
      return new StringLiteralToken(null, value.asStringLiteral().value);
    case NULL:
      return new Token(TokenKind.JS_NULL, null);
    case TRUE:
      return new Token(TokenKind.JS_TRUE, null);
    default:
      throw new RuntimeException("unexpected literal kind");
    }
  }

  @Override
  protected ParseTree transform(NewExpressionTree tree) {
    return createNew(transformAny(tree.constructor), transform(tree.arguments));
  }

  @Override
  protected ParseTree transform(ParameterDeclarationTree tree) {
    throw new RuntimeException("params should get transformed by formals");
  }

  @Override
  protected ParseTree transform(ParenExpressionTree tree) {
    return createParenExpression(transformAny(tree.expression));
  }

  @Override
  protected ParseTree transform(PostfixExpressionTree tree) {
    return createPostfixExpression(transformAny(tree.operand), transformOperatorToken(tree.operator));
  }

  @Override
  protected ParseTree transform(ReturnStatementTree tree) {
    return createReturnStatement(transformAny(tree.value));
  }

  @Override
  protected ParseTree transform(SwitchStatementTree tree) {
    return createSwitchStatement(transformAny(tree.expression), transformList(tree.caseClauses));
  }

  @Override
  protected ParseTree transform(ThisExpressionTree tree) {
    return createThis();
  }

  @Override
  protected ParseTree transform(ThrowStatementTree tree) {
    return createThrowStatement(transformAny(tree.expression));
  }

  @Override
  protected ParseTree transform(TryStatementTree tree) {
    omakase.syntax.trees.javascript.CatchClauseTree catchClause = null;
    if (tree.catchClause != null) {
      catchClause = transform(tree.catchClause);
    }
    omakase.syntax.trees.javascript.BlockTree finallyClause = null;
    if (tree.finallyClause != null) {
      finallyClause = transform(tree.finallyClause);
    }
    return createTryStatement(transform(tree.body), catchClause, finallyClause);
  }

  @Override
  protected ParseTree transform(UnaryExpressionTree tree) {
    return createUnaryExpression(transformOperatorToken(tree.operator), transformAny(tree.operand));
  }

  @Override
  protected ParseTree transform(VariableDeclarationTree tree) {
    return createVariableDeclaration(tree.name.value, transformAny(tree.initializer));
  }

  @Override
  protected ParseTree transform(VariableStatementTree tree) {
    return createVariableStatement(transformList(tree.declarations));
  }

  @Override
  protected ParseTree transform(WhileStatementTree tree) {
    return createWhileStatement(transformAny(tree.condition), transformAny(tree.body));
  }

  @Override
  protected ParseTree transform(MethodDeclarationTree tree) {
    throw new RuntimeException("Methods should be handled by class transformer");
  }

  @Override
  protected ParseTree transform(ClassDeclarationTree tree) {
    return new ClassTransformer(tree).transformClass();
  }

  private class ClassTransformer {
    private final ClassDeclarationTree classTree;
    private final ImmutableList.Builder<ParseTree> members = new ImmutableList.Builder<ParseTree>();

    public ClassTransformer(ClassDeclarationTree tree) {
      classTree = tree;
    }

    private String getClassName() {
      return classTree.name.value;
    }

    // class C { members } =>
    //
    //  (function() {
    //    C = function() {};
    //    C.prototype.member = ...;
    //  }());
    //
    public ParseTree transformClass() {
      createConstructor();
      createMembers();
      return createScopedBlock(members.build());
    }

    private void createConstructor() {
      members.add(createAssignmentStatement(
          createIdentifier(getClassName()),
          createFunction(createFormalParameterList(), createBlock())
      ));
    }

    private void createMembers() {
      for (ParseTree member: classTree.members) {
        createMember(member.asMethodDeclaration());
      }
    }

    private void createMember(MethodDeclarationTree method) {
      members.add(createProtoMember(getClassName(), method.name.value,
          createFunction(createFormalParameterList(), transformAny(method.body).asJavascriptBlock())));
    }
  }
}
