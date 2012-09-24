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
import omakase.syntax.trees.ParseTree;

/**
 */
public class LocalVariableSymbol extends Symbol {
  private Type type;

  public LocalVariableSymbol(String name, ParseTree tree, Type type) {
    super(SymbolKind.LOCAL_VARIABLE, name, tree);
    this.type = type;
  }

  @Override
  public Type getType() {
    return type;
  }

  public void setInferredType(Type type) {
    if (this.type != null) {
      throw new RuntimeException("Cannot infer variable's type more than once.");
    }
    this.type = type;
  }

  @Override
  public boolean isWritable() {
    // TODO: const locals.
    return true;
  }
}
