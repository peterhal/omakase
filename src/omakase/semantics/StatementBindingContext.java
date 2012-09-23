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

/**
 */
public abstract class StatementBindingContext extends ExpressionBindingContext {
  private final StatementBindingContext outerContext;

  public StatementBindingContext(Project project, BindingResults results) {
    super(project, results);
    outerContext = null;
  }

  protected StatementBindingContext(StatementBindingContext outerContext) {
    super(outerContext.project, outerContext.getResults());
    this.outerContext = outerContext;
  }

  public boolean hasBreakLabel() {
    return outerContext.hasBreakLabel();
  }

  public boolean hasContinueLabel() {
    return outerContext.hasContinueLabel();
  }

  public Type getSwitchExpressionType() {
    throw new RuntimeException("Parser should ensure case labels never occur outside of switch statements.");
  }

  public boolean canReturn() {
    return outerContext.canReturn();
  }

  public Type getReturnType() {
    return outerContext.getReturnType();
  }

  @Override
  public Symbol lookupIdentifier(String value) {
    return outerContext == null ? null : outerContext.lookupIdentifier(value);
  }

  public boolean containsLocal(String name) {
    Symbol result = lookupIdentifier(name);
    return result == null ? false : result.isLocalVariable() || result.isParameter();
  }
}
