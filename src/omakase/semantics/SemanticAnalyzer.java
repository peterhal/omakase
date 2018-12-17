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

import omakase.syntax.Parser;
import omakase.syntax.PredefinedNames;
import omakase.syntax.trees.SourceFileTree;
import omakase.util.SourceFile;
import omakase.util.SourceLocation;

/**
 */
public class SemanticAnalyzer {
  private final Project project;

  public SemanticAnalyzer(Project project) {
    this.project = project;
  }

  private void parseProject() {
    for (SourceFile file : project.files()) {
      SourceFileTree tree = Parser.parse(project.errorReporter(), file);
      project.setParseTree(file,tree);
    }
  }

  public void analyze() {
    parseProject();
    if (hadError()) {
      return;
    }

    declareGlobals();
    if (hadError()) {
      return;
    }

    // TODO: Declare Type Parameter Constraints

    declareClassMembers();
    if (hadError()) {
      return;
    }

    checkTypes();
    if (hadError()) {
      return;
    }

    checkMain();
    if (hadError()) {
      return;
    }
  }

  // TODO: Extract this into checker specific to node.js targets.
  private void checkMain() {
    var main = project.getSymbol(PredefinedNames.MAIN);
    if (main == null) {
      project.errorReporter().reportError("Missing definition of 'main'.");
    } else if (main.isFunction()) {
      var mainFunction = main.asFunction();
      if (!mainFunction.parameters.isEmpty()) {
        project.errorReporter().reportError(main.location.location.start, "'main' may not have parameters.");
      }
      if (!mainFunction.type.returnType.isVoidType()) {
        project.errorReporter().reportError(main.location.location.start, "'main' must return 'void'.");
      }
    } else {
      project.errorReporter().reportError(main.location.location.start, "'main' must be a function");
    }
  }

  private void declareGlobals() {
    GlobalDeclarer.declare(project);
  }

  private void declareClassMembers() {
    new ClassMemberDeclarer(project).declareMembers();
  }

  private void checkTypes() {
    new TypeChecker(project).checkAllTypes();
  }

  private boolean hadError() {
    return project.errorReporter().hadError();
  }
}
