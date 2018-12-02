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

import org.joda.primitives.collection.impl.ArrayIntCollection;

import java.util.Arrays;

/**
 * Maps offsets in a string to line and column.
 */
public class LineOffsetTable {

  private final int[] offsets;

  /**
   * @param value The string to create a line offset table for.
   */
  public LineOffsetTable(String value) {
    ArrayIntCollection offsets = new ArrayIntCollection();
    offsets.add(0);
    for (int i = 0; i < value.length(); i++) {
      switch (value.charAt(i)) {
      case '\r':
        if ((i + 1) < value.length() && value.charAt(i + 1) == '\n') {
          i++;
        }
        // fallthrough
      case '\n':
      case '\u2028':
      case '\u2029':
        i++;
        offsets.add(i);
        break;
      }
    }
    offsets.add(Integer.MAX_VALUE);

    this.offsets = offsets.toIntArray();
  }

  /**
   * Returns the line of a given character offset.
   */
  public int line(int offset) {
    int index = Arrays.binarySearch(offsets, offset);
    return (index >= 0) ? index : - index - 2;
  }

  /**
   * Returns the column of a given character offset.
   */
  public int column(int offset) {
    return offset - offsetOfLine(line(offset));
  }

  /**
   * Returns the starting offset of a line.
   */
  public int offsetOfLine(int line) {
    return offsets[line];
  }
}
