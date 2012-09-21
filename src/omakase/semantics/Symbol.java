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

package omakase.semantics;

import omakase.syntax.trees.ParseTree;

/**
 */
public abstract class Symbol {
  public final String name;
  public final SymbolKind kind;
  public final ParseTree location;

  public Symbol(SymbolKind kind, String name, ParseTree location) {
    this.kind = kind;
    this.name = name;
    this.location = location;
  }

  public MethodSymbol asMethod() {
    return (MethodSymbol) this;
  }

  public FieldSymbol asField() {
    return (FieldSymbol) this;
  }

  public abstract Type getType();

  public boolean isClassSymbol() {
    return kind == SymbolKind.CLASS;
  }
}
