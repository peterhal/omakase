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
import omakase.syntax.trees.ParameterDeclarationTree;

/**
 */
public class ParameterSymbol extends Symbol {
  public final Type type;
  private final boolean isExtern;

  public ParameterSymbol(ParameterDeclarationTree tree, Type type, boolean isExtern) {
    super(SymbolKind.PARAMETER, tree.name.value, tree);
    this.type = type;
    this.isExtern = isExtern;
  }

  @Override
  public boolean isExtern() {
    return this.isExtern;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public boolean isWritable() {
    // TODO: Const parameters.
    return true;
  }
}
