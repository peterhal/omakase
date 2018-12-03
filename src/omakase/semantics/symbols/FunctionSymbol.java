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

import omakase.semantics.types.FunctionType;
import omakase.semantics.types.Type;
import omakase.syntax.trees.FunctionDeclarationTree;
import omakase.syntax.trees.MethodDeclarationTree;

import java.util.Map;

/**
 */
public class FunctionSymbol extends Symbol {
  public final FunctionDeclarationTree tree;
  public final FunctionType type;
  public final Map<String, ParameterSymbol> parameters;

  public FunctionSymbol(FunctionDeclarationTree tree, FunctionType type, Map<String, ParameterSymbol> parameters) {
    super(SymbolKind.FUNCTION, tree.name.value, tree);
    this.tree = tree;
    this.type = type;
    this.parameters = parameters;
  }

  @Override
  public boolean isExtern() {
    return tree.isExtern;
  }

  @Override
  public Type getType() {
    return type;
  }

  public Type getReturnType() {
    return type.returnType;
  }

  @Override
  public boolean isWritable() {
    return false;
  }
}