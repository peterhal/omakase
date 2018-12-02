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
        project.getLookupContext(),
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

  @Override
  public boolean hasBreakLabel() {
    return false;
  }

  @Override
  public boolean hasContinueLabel() {
    return false;
  }

  @Override
  public boolean canReturn() {
    return true;
  }

  @Override
  public Type getReturnType() {
    return method.getReturnType();
  }

  @Override
  public boolean hasThis() {
    return !method.isStatic;
  }

  @Override
  public Type getThisType() {
    return getTypes().getClassType(method.parent);
  }

  @Override
  public Symbol lookupIdentifier(String value) {
    Symbol result = method.parameters.get(value);
    return result == null ? super.lookupIdentifier(value) : result;
  }
}
