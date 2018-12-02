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

import omakase.semantics.symbols.ClassSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.syntax.tokens.Token;
import omakase.syntax.trees.ClassDeclarationTree;
import omakase.syntax.trees.FunctionDeclarationTree;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.SourceFileTree;
import omakase.util.SourceLocation;

/**
 */
public class GlobalDeclarer {
  private final Project project;

  public GlobalDeclarer(Project project) {
    this.project = project;
  }

  public static void declare(Project project) {
    new GlobalDeclarer(project).declare();
  }

  public void declare() {
    for (SourceFileTree tree : project.trees()) {
      declare(tree);
    }
  }

  private void declare(SourceFileTree tree) {
    for (ParseTree element : tree.declarations) {
      if (element.isClassDeclaration()) {
        ClassDeclarationTree classDeclaration = element.asClassDeclaration();
        String className = classDeclaration.name.value;
        if (project.containsSymbol(className)) {
          reportError(classDeclaration.name, "Duplicate class '%s'.", className);
          reportRelatedError(project.getSymbol(className));
          return;
        }
        project.addClass(new ClassSymbol(className, classDeclaration, project.getTypes().getClassSymbolType()));
      }  else if (element.isFunctionDeclaration()) {
        FunctionDeclarationTree functionDeclaration = element.asFunctionDeclaration();

      } else {
        throw new RuntimeException("Unexpected global parse tree");
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
