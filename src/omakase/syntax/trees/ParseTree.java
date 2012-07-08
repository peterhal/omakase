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

  public ClassDeclarationTree asClassDeclaration() {
    return (ClassDeclarationTree) this;
  }

  public MethodDeclarationTree asMethodDeclaration() {
    return (MethodDeclarationTree) this;
  }

  public ParameterDeclarationTree asParameterDeclaration() {
    return (ParameterDeclarationTree) this;
  }

  public BlockTree asBlock() {
    return (BlockTree) this;
  }

  public ExpressionStatementTree asExpressionStatement() {
    return (ExpressionStatementTree) this;
  }

  public LiteralExpressionTree asLiteralExpression() {
    return (LiteralExpressionTree) this;
  }

  public SimpleNameExpressionTree asSimpleNameExpression() {
    return (SimpleNameExpressionTree) this;
  }

  public CallExpressionTree asCallExpression() {
    return (CallExpressionTree) this;
  }

  public SourceFileTree asSourceFile() {
    return (SourceFileTree) this;
  }

  public omakase.syntax.trees.javascript.ProgramTree asJavascriptProgram() {
    return (omakase.syntax.trees.javascript.ProgramTree) this;
  }

  public FormalParameterListTree asFormalParameterList() {
    return (FormalParameterListTree) this;
  }

  public FunctionExpressionTree asFunctionExpression() {
    return (FunctionExpressionTree) this;
  }

  public ParenExpressionTree asParenExpression() {
    return (ParenExpressionTree) this;
  }

  public BinaryExpressionTree asBinaryExpression() {
    return (BinaryExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.BinaryExpressionTree asJavascriptBinaryExpression() {
    return (omakase.syntax.trees.javascript.BinaryExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.BlockTree asJavascriptBlock() {
    return (omakase.syntax.trees.javascript.BlockTree) this;
  }

  public omakase.syntax.trees.javascript.CallExpressionTree asJavascriptCallExpression() {
    return (omakase.syntax.trees.javascript.CallExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ExpressionStatementTree asJavascriptExpressionStatement() {
    return (omakase.syntax.trees.javascript.ExpressionStatementTree) this;
  }

  public omakase.syntax.trees.javascript.IdentifierExpressionTree asJavascriptIdentifierExpression() {
    return (omakase.syntax.trees.javascript.IdentifierExpressionTree) this;
  }
  public omakase.syntax.trees.javascript.FormalParameterListTree asJavascriptFormalParameterList() {
    return (omakase.syntax.trees.javascript.FormalParameterListTree) this;
  }

  public omakase.syntax.trees.javascript.FunctionExpressionTree asJavascriptFunctionExpression() {
    return (omakase.syntax.trees.javascript.FunctionExpressionTree) this;
  }

  public omakase.syntax.trees.javascript.ParenExpressionTree asJavascriptParenExpression() {
    return (omakase.syntax.trees.javascript.ParenExpressionTree) this;
  }

}
