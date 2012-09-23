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
import omakase.util.ErrorReporter;

/**
 */
public class ExpressionBindingContext {
  public final Project project;
  private final Type thisType;
  private final BindingResults results;

  public ExpressionBindingContext(Project project, BindingResults results) {
    this.project = project;
    this.results = results;
    this.thisType = null;
  }

  public Types getTypes() {
    return project.getTypes();
  }

  public ErrorReporter errorReporter() {
    return project.errorReporter();
  }

  public boolean hasThis() {
    return thisType != null;
  }

  public Type getThisType() {
    return thisType;
  }

  public Symbol lookupIdentifier(String value) {
    return null;
  }

  public BindingResults getResults() {
    return results;
  }
}