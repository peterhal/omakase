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
import omakase.syntax.trees.SourceFileTree;
import omakase.util.SourceFile;

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

    declareClasses();
    if (hadError()) {
      return;
    }

    declareClassMembers();
    if (hadError()) {
      return;
    }

    checkTypes();
    if (hadError()) {
      return;
    }
  }

  private void declareClasses() {
    new ClassDeclarer(project).declareClasses();
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
