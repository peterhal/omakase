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

import omakase.semantics.types.Type;
import omakase.syntax.trees.FunctionExpressionTree;
import omakase.syntax.trees.ParseTree;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ReturnInferenceContext {
  private final FunctionExpressionTree tree;
  public final Map<ParseTree, Type> returns = new HashMap<ParseTree, Type>();

  public ReturnInferenceContext(FunctionExpressionTree tree) {
    this.tree = tree;
  }

  public void addReturn(ParseTree tree, Type type) {
    returns.put(tree, type);
  }
}
