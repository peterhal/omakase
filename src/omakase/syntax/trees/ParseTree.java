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
}
