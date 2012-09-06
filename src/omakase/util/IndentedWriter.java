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

package omakase.util;

import java.io.PrintStream;

public class IndentedWriter {
  private int indent;
  private PrintStream out;
  private boolean startOfLine;
  private final int INDENT = 2;

  public IndentedWriter(PrintStream out) {
    this.indent = 0;
    this.out = out;
    this.startOfLine = true;
  }

  public void writeLine() {
    out.println();
    this.startOfLine = true;
  }

  public void write(String value) {
    if (this.startOfLine) {
      for (int i = 0; i < indent; i++) {
        out.print(' ');
      }
      this.startOfLine = false;
    } else {
      out.print(' ');
    }
    out.print(value);
  }

  public void outdent() {
    indent -= INDENT;
  }

  public void indent() {
    indent += INDENT;
  }
}