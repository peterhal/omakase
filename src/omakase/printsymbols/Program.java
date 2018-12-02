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

package omakase.printsymbols;

import omakase.semantics.*;
import omakase.semantics.symbols.ClassSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.util.ConsoleErrorReporter;
import omakase.util.IndentedWriter;

/**
 */
public class Program {
  public static void main(String[] args) {
    Project project = ProjectReader.readProject(args, ConsoleErrorReporter.reporter);
    if (project == null) {
      return;
    }

    SemanticAnalyzer analyzer = new SemanticAnalyzer(project);
    analyzer.analyze();

    IndentedWriter out = new IndentedWriter(System.out);
    for (Symbol symbol : project.getSymbols()) {
      out.write(symbol.name);
      out.writeLine();
      out.indent();

      if (symbol.isClass()) {
        for (Symbol member : symbol.asClass().members()) {
          out.write(member.name);
          out.writeLine();
        }
      }

      out.outdent();
    }
  }
}
