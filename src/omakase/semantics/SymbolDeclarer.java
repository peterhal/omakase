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

import omakase.semantics.symbols.ClassSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.Type;
import omakase.semantics.types.TypeContainer;
import omakase.syntax.trees.ParseTree;

public class SymbolDeclarer {
  protected final Project project;

  public SymbolDeclarer(Project project) {
    this.project = project;
  }

  protected Type bindType(ParseTree type) {
    return new TypeBinder(project).bindType(type);
  }

  private TypeContainer types() {
    return project.getTypes();
  }

  protected void reportError(ParseTree tree, String message, Object... args) {
    project.errorReporter().reportError(tree.location.start, message, args);
  }

  protected boolean checkForDuplicateMember(ClassSymbol clazz, String name, ParseTree tree) {
    Symbol member = clazz.getMember(name);
    if (member != null) {
      reportError(tree, "Duplicate member '%s' in class '%s'.", name, clazz);
      reportError(member.location, "Location of duplicate member.");
      return true;
    }
    return false;
  }
}
