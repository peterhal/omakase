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

import com.google.common.base.Preconditions;

/**
 * A location in a source file. Contains the source file, char offset, line and column within the
 * file. Note that offset, line and column are all 0 based offsets.
 *
 * SourceLocations are immutable.
 */
public class SourceLocation {
  public final SourceFile file;
  public final int offset;

  public SourceLocation(SourceFile file, int offset) {
    Preconditions.checkArgument(offset >= 0 && offset <= (file.length() + 1));

    this.file = file;
    this.offset = offset;
  }

  public boolean isBefore(SourceLocation other) {
    if (this.file != other.file) {
      throw new RuntimeException("Cannot compare locations from different files.");
    }
    return this.offset < other.offset;
  }

  @Override
  public String toString() {
    return String.format("%s (%d, %d)", file.name, line() + 1, column() + 1);
  }

  /**
   * @return The line number of this location.
   */
  public int line() {
    return file.lineOfOffset(offset);
  }

  /**
   * @return The column number of this location.
   */
  public int column() {
    return file.columnOfOffset(offset);
  }
}
