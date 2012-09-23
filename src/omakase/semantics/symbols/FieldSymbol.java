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

package omakase.semantics.symbols;

import omakase.semantics.types.Type;
import omakase.syntax.trees.VariableDeclarationTree;

/**
 */
public class FieldSymbol extends Symbol {
  public final ClassSymbol parent;
  public final VariableDeclarationTree tree;
  public final Type type;
  public final boolean isStatic;

  public FieldSymbol(ClassSymbol parent, String name, VariableDeclarationTree tree, Type type, boolean isStatic) {
    super(SymbolKind.FIELD, name, tree);
    this.parent = parent;
    this.tree = tree;
    this.type = type;
    this.isStatic = isStatic;
    parent.addMember(this);
  }

  @Override
  public Type getType() {
    return type;
  }
}
