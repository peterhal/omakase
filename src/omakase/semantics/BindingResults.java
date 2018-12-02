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
  private static class BindingResult {
    public ParseTree tree;
    public Type type;
    public Symbol symbol;
    public boolean isWritable;
  }
  private final Map<ParseTree, BindingResult> results;

  public BindingResults() {
    this.results = new HashMap<ParseTree, BindingResult>();
  }

  private BindingResult getResult(ParseTree tree) {
    BindingResult result = results.get(tree);
    if (result == null) {
      result = new BindingResult();
      result.tree = tree;
      results.put(tree, result);
    }
    return result;
  }

  public Type getType(ParseTree tree) {
    return getResult(tree).type;
  }

  public void setType(ParseTree tree, Type type) {
    getResult(tree).type = type;
  }

  public Symbol getSymbol(ParseTree tree) {
    return getResult(tree).symbol;
  }

  public void setSymbol(ParseTree tree, Symbol symbol) {
    getResult(tree).symbol = symbol;
  }

  public boolean isWritable(ParseTree tree) {
    return getResult(tree).isWritable;
  }

  public void setWritable(ParseTree tree, boolean writable) {
    getResult(tree).isWritable = writable;
  }
}
