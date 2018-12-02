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
 * A range of characters in a source file. Represented as the start and ending location.
 *
 * Source ranges are immutable.
 */
public class SourceRange {
  public final SourceLocation start;
  public final SourceLocation end;

  public SourceRange(SourceFile file) {
    this(file, 0, file.length());
  }

  public SourceRange(SourceFile file, int startOffset, int endOffset) {
    this(new SourceLocation(file, startOffset), new SourceLocation(file, endOffset));
  }

  /**
   * Start and end of a source range must be in the same file.
   * Start offset must be before the end offset in a source range.
   */
  public SourceRange(SourceLocation start, SourceLocation end) {
    Preconditions.checkArgument(start.file == end.file);
    Preconditions.checkArgument(start.offset <= end.offset);

    this.start = start;
    this.end = end;
  }

  public SourceFile file() {
    return this.start.file;
  }

  @Override
  public String toString() {
    return start.toString();
  }
}
