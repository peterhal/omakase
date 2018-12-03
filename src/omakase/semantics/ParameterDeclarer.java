// Copyright 2018 Peter Hallam
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

import com.google.common.collect.ImmutableMap;
import omakase.semantics.symbols.ParameterSymbol;
import omakase.semantics.types.Type;
import omakase.syntax.trees.FormalParameterListTree;
import omakase.syntax.trees.ParameterDeclarationTree;

import java.util.HashMap;
import java.util.Map;

public class ParameterDeclarer extends SymbolDeclarer {
  public ParameterDeclarer(Project project) {
    super(project);
  }

  protected Map<String, ParameterSymbol> buildParameters(FormalParameterListTree formals) {
    Map<String, ParameterSymbol> parameters = new HashMap<String, ParameterSymbol>();
    for (ParameterDeclarationTree tree : formals.parameters) {
      String name = tree.name.value;
      Type type = null;
      if (tree.type != null) {
        type = bindType(tree.type);
      }
      if (parameters.containsKey(name)) {
        reportError(tree, "Duplicate parameter '%s'.", name);
      } else {
        ParameterSymbol parameter = new ParameterSymbol(tree, type);
        project.bindings.setSymbol(tree, parameter);
        parameters.put(name, parameter);
      }
    }
    return ImmutableMap.copyOf(parameters);
  }
}
