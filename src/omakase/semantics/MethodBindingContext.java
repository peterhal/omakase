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

import omakase.semantics.symbols.MethodSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.Type;

/**
 */
public class MethodBindingContext extends StatementBindingContext {
  private final MethodSymbol method;

  public MethodBindingContext(Project project, MethodSymbol method) {
    super(
        project,
        null,
        new BindingResults(),
        new ScopedLookupContext(project.getLookupContext(), new ParameterLookupContext(method.parameters)),
        method.isStatic
            ? null
            : project.getTypes().getClassType(method.parent),
        false,
        false,
        null,
        true,
        method.getReturnType());
    this.method = method;
  }
}
