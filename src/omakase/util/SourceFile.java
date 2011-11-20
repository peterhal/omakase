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
 * A source file to be compiled.
 *
 * SourceFiles appear immutable, though the line/column mapping is built on demand.
 */
public class SourceFile {
  public final String name;
  public final String contents;
  private LineOffsetTable lineOffsets;

  public SourceFile(String name, String contents) {
    this.name = name;
    this.contents = contents;
  }

  public int length() {
    return contents.length();
  }

  public int lineOfOffset(int offset) {
    ensureLineOffsetTable();
    return lineOffsets.line(offset);
  }

  public int columnOfOffset(int offset) {
    ensureLineOffsetTable();
    return lineOffsets.column(offset);
  }

  private void ensureLineOffsetTable() {
    if (this.lineOffsets == null) {
      this.lineOffsets = new LineOffsetTable(contents);
    }
  }

}
