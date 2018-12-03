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

package omakase.codegeneration;

import omakase.printtree.ParseTreeWriter;

import java.io.PrintStream;

public class OptimizedTreeWriter  extends ParseTreeWriter {
  public OptimizedTreeWriter(PrintStream out) {
    super(out);
  }

  protected void writeLine() {
    write(" ");
  }

  protected void outdent() {}

  protected void indent() {}
}
