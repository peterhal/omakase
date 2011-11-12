// Copyright 2011 Peter Hallam
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

/**
 * A location in a source file.
 */
public class SourceLocation {
  public final SourceFile file;
  public final int offset;

  public SourceLocation(SourceFile file, int offset) {
    this.file = file;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return String.format("%s (%d, %d)", file.name, line(), column());
  }

  public int line() {
    return file.lineOfOffset(offset);
  }

  public int column() {
    return file.columnOfOffset(offset);
  }
}
