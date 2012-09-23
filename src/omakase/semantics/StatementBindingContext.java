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

import omakase.semantics.symbols.LocalVariableSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.Type;

import java.util.Map;

/**
 */
public class StatementBindingContext extends ExpressionBindingContext {
  private final boolean hasBreak;
  private final boolean hasContinue;
  private final Type switchType;
  private final boolean canReturn;
  private final Type returnType;

  public StatementBindingContext(
      Project project,
      BindingResults results,
      IdentifierLookupContext lookupContext,
      Type thisType,
      boolean hasBreak,
      boolean hasContinue,
      Type switchType,
      boolean canReturn,
      Type returnType) {
    super(project, results, lookupContext, thisType);
    this.hasBreak = hasBreak;
    this.hasContinue = hasContinue;
    this.switchType = switchType;
    this.canReturn = canReturn;
    this.returnType = returnType;
  }

  public StatementBindingContext createLoopContext() {
    return new StatementBindingContext(
        this.project,
        this.getResults(),
        this.lookupContext,
        this.getThisType(),
        true,
        true,
        this.switchType,
        this.canReturn,
        this.returnType);
  }

  public StatementBindingContext createFinallyContext() {
    return new StatementBindingContext(
        this.project,
        this.getResults(),
        this.lookupContext,
        this.getThisType(),
        false,
        false,
        this.switchType,
        false,
        this.returnType);
  }

  public StatementBindingContext createSwitchContext(Type switchExpressionType) {
    return new StatementBindingContext(
        this.project,
        this.getResults(),
        this.lookupContext,
        this.getThisType(),
        true,
        this.hasContinue,
        switchExpressionType,
        this.canReturn,
        this.returnType);
  }

  public StatementBindingContext createLookupContext(IdentifierLookupContext context) {
    return new StatementBindingContext(
        this.project,
        this.getResults(),
        context,
        this.getThisType(),
        this.hasBreak,
        this.hasContinue,
        this.switchType,
        this.canReturn,
        this.returnType);
  }

  public boolean hasBreakLabel() {
    return hasBreak;
  }

  public boolean hasContinueLabel() {
    return hasContinue;
  }

  public Type getSwitchExpressionType() {
    // Parser ensures case labels never occur outside of switch statements.
    return switchType;
  }

  public boolean canReturn() {
    return this.canReturn;
  }

  public Type getReturnType() {
    return returnType;
  }

  public boolean containsLocal(String name) {
    Symbol result = lookupIdentifier(name);
    return result != null && (result.isLocalVariable() || result.isParameter());
  }
}
