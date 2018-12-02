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

import omakase.semantics.symbols.FunctionSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.Type;

public class FunctionBindingContext extends StatementBindingContext {
  private FunctionSymbol function;

  public FunctionBindingContext(Project project, FunctionSymbol function) {
    super(project,
        null,
        new BindingResults(),
        project.getLookupContext(),
        null,
        false,
        false,
        null,
        true,
        function.getReturnType());
    this.function = function;
  }

  @Override
  public Symbol lookupIdentifier(String value) {
    Symbol result = function.parameters.get(value);
    return result == null ? super.lookupIdentifier(value) : result;
  }
}
