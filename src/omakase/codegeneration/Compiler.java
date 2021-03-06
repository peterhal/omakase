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

package omakase.codegeneration;

import omakase.printtree.ParseTreeWriter;
import omakase.semantics.Project;
import omakase.semantics.SemanticAnalyzer;
import omakase.syntax.trees.ParseTree;

/**
 */
public class Compiler {
  private final Project project;

  public Compiler(Project project) {
    this.project = project;
  }

  public void compile() {
    SemanticAnalyzer analyzer = new SemanticAnalyzer(project);
    analyzer.analyze();
    if (!project.errorReporter().hadError()) {
      writeProject();
    }
  }

  private void writeProject() {
    if (project.isDebug()) {
      writeProjectDebug();
    } else {
      writeProjectOptimized();
    }
  }

  private void writeProjectOptimized() {
    ParseTreeWriter writer = new ParseTreeWriter(System.out);
    new OptimizedJavascriptTransformer(project).write(writer);
  }

  private void writeProjectDebug() {
    for (ParseTree tree : project.trees()) {
      JavascriptTransformer transformer = new JavascriptTransformer();
      ParseTree result = transformer.transformAny(tree);
      ParseTreeWriter writer = new ParseTreeWriter(System.out);
      writer.visitAny(result);
    }
  }
}
