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

import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.Type;
import omakase.syntax.trees.ParseTree;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Should create expression trees rather than these maps.
 */
public class BindingResults {
  private final Map<ParseTree, Type> expressionTypes;
  private final Map<ParseTree, Symbol> expressionSymbols;

  public BindingResults() {
    this.expressionTypes = new HashMap<ParseTree, Type>();
    this.expressionSymbols = new HashMap<ParseTree, Symbol>();
  }

  public Type getType(ParseTree tree) {
    return expressionTypes.get(tree);
  }

  public void setType(ParseTree tree, Type type) {
    expressionTypes.put(tree, type);
  }

  public Symbol getSymbol(ParseTree tree) {
    return expressionSymbols.get(tree);
  }

  public void setSymbol(ParseTree tree, Symbol symbol) {
    expressionSymbols.put(tree, symbol);
  }
}
