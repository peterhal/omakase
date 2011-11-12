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
 *
 */
public class LineOffsetTable {

  private final int[] offsets;

  public LineOffsetTable(String contents) {
    ArrayIntCollection offsets = new ArrayIntCollection();
    offsets.add(0);
    for (int i = 0; i < contents.length(); i++) {
      switch (contents.charAt(i)) {
      case '\r':
        if ((i + 1) < contents.length() && contents.charAt(i + 1) == '\n') {
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

  public int line(int offset) {
    int index = Arrays.binarySearch(offsets, offset);
    return (index >= 0) ? index : - index - 2;
  }

  public int column(int offset) {
    return offset - offsetOfLine(line(offset));
  }

  public int offsetOfLine(int line) {
    return offsets[line];
  }
}
