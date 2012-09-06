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

import omakase.syntax.tokens.IdentifierToken;
import omakase.syntax.tokens.Token;
import omakase.syntax.trees.ClassDeclarationTree;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.SourceFileTree;
import omakase.util.SourceLocation;

/**
 */
public class ClassDeclarer {
  private final Project project;

  public ClassDeclarer(Project project) {
    this.project = project;
  }

  public void declareClasses() {
    for (SourceFileTree tree : project.trees()) {
      declareClasses(tree);
    }
  }

  private void declareClasses(SourceFileTree tree) {
    for (ParseTree element : tree.declarations) {
      if (tree.isClassDeclaration()) {
        ClassDeclarationTree classDeclaration = tree.asClassDeclaration();
        String className = classDeclaration.name.value;
        if (project.containsClass(className)) {
          reportError(classDeclaration.name, "Duplicate class '%s'.", className);
          reportRelatedError(project.getClass(className));
          return;
        }
        project.addClass(new ClassSymbol(className, classDeclaration));
      }
    }
  }

  private void reportRelatedError(Symbol symbol) {
    reportError(symbol.location.start(), "Related location");
  }

  private void reportError(Token token, String format, Object... args) {
    reportError(token.start(), format, args);
  }

  private void reportError(SourceLocation location, String format, Object... args) {
    project.errorReporter().reportError(location, format, args);
  }
}
