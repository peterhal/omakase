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

package omakase.syntax;

import com.google.common.collect.ImmutableList;
import omakase.syntax.trees.ParseTree;

/**
 *
 */
public class ParseTreeTransformer {

  private <T extends ParseTree> ImmutableList<T> transformList(ImmutableList<T> trees) {
    ImmutableList.Builder<T> result = null;
    for (int i = 0; i < trees.size(); i++) {
      ParseTree element = transformAny(trees.get(i));
      if (result == null && element != trees.get(i)) {
        result = new ImmutableList.Builder<T>();
        for (int j = 0; j < i; j++) {
          result.add(trees.get(j));
        }
      }
      if (result != null) {
        result.add((T)element);
      }
    }
    if (result == null) {
      return trees;
    }
    return result.build();
  }

  public ParseTree transformAny(ParseTree tree) {
    if (tree == null) {
      return null;
    }

    switch (tree.kind) {
    case JAVASCRIPT_PROGRAM:
      return transform(tree.asJavascriptProgram());
    default:
      throw new RuntimeException("Unexpected tree kind.");
    }
  }

  private ParseTree transform(omakase.syntax.trees.javascript.ProgramTree tree) {
    ImmutableList<ParseTree> sourceElements = transformList(tree.sourceElements);
    if (sourceElements == tree.sourceElements) {
      return tree;
    }

    return new omakase.syntax.trees.javascript.ProgramTree(null, sourceElements);
  }
}
