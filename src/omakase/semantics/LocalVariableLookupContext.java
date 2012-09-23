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

import com.google.common.collect.ImmutableMap;
import omakase.semantics.symbols.LocalVariableSymbol;
import omakase.semantics.symbols.Symbol;

import java.util.Map;

/**
*/
class LocalVariableLookupContext implements IdentifierLookupContext {
  private final IdentifierLookupContext outerContext;
  private final Map<String, LocalVariableSymbol> locals;

  public LocalVariableLookupContext(IdentifierLookupContext outerContext, Map<String, LocalVariableSymbol> locals) {
    this.outerContext = outerContext;
    this.locals = locals;
  }

  public LocalVariableLookupContext(IdentifierLookupContext outerContext, LocalVariableSymbol local) {
    this(outerContext, ImmutableMap.of(local.name, local));
  }

  public Symbol lookupIdentifier(String value) {
    Symbol result = locals.get(value);
    return result != null ? result : outerContext.lookupIdentifier(value);
  }
}
