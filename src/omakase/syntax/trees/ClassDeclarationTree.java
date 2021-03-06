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

import com.google.common.collect.ImmutableList;
import omakase.syntax.tokens.IdentifierToken;
import omakase.util.SourceRange;

/**
 *
 */
public class ClassDeclarationTree extends ParseTree {
  public final boolean isExtern;
  public final IdentifierToken name;
  public final ImmutableList<? extends ParseTree> typeParameters;
  public final ImmutableList<? extends ParseTree> members;

  public ClassDeclarationTree(SourceRange range, boolean isExtern, IdentifierToken name, ImmutableList<? extends ParseTree> typeParameters, ImmutableList<? extends ParseTree> members) {
    super(range, ParseTreeKind.CLASS_DECLARATION);
    this.isExtern = isExtern;
    this.name = name;
    this.typeParameters = typeParameters;
    this.members = members;
  }
}
