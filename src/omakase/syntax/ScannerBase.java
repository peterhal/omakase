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

package omakase.syntax;

import omakase.syntax.tokens.Token;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;
import omakase.util.SourceLocation;
import omakase.util.SourceRange;

/**
 * Base class for building scanners.
 */
public abstract class ScannerBase {
  protected final ErrorReporter reporter;
  protected final SourceRange source;
  protected int index;
  protected final SourceFile file;
  protected final int end;
  protected final StringBuilder buffer = new StringBuilder();

  public ScannerBase(SourceRange source, ErrorReporter reporter) {
    this.source = source;
    this.index = source.start.offset;
    this.end = source.end.offset;
    this.file = source.file();
    this.reporter = reporter;
  }

  public abstract Token scanToken();

  public void setPosition(int index) {
    this.index = index;
  }

  public int getPosition() {
    return this.index;
  }

  /**
   * Report an error.
   * @param index The index in the source file to report the error at.
   * @param format A format message to report.
   * @param args The arguments of the format.
   */
  protected void reportError(int index, String format, Object... args) {
    this.reporter.reportError(getLocation(index), format, args);
  }

  /**
   * @return A SourceRange from start to the current position within the current file.
   */
  protected SourceRange getRange(int start) {
    return getRange(start, index);
  }

  /**
   * @return A SourceRange from start to end within the current file.
   */
  private SourceRange getRange(int start, int end) {
    return new SourceRange(getLocation(start), getLocation(end));
  }

  /**
   * @return A SourceLocation within the file for the given index.
   */
  private SourceLocation getLocation(int index) {
    return new SourceLocation(file, index);
  }

  /**
   * Tests the next character. If the next character is equal to the expected character, then
   * advance past it, otherwise return false and do not advance.
   * @param expected The character to test for.
   * @return True if the next character was the expected character, false otherwise.
   */
  protected boolean eatOpt(char expected) {
    if (peekChar() == expected) {
      nextChar();
      return true;
    }
    return false;
  }

  /**
   * Advances the current position in the file one character.
   * @return The character advanced over.
   */
  protected char nextChar() {
    char ch = peekChar();
    index++;
    return ch;
  }

  /**
   * @return True if the index-th next character is equal to ch.
   */
  protected boolean peek(char ch, int index) {
    return peekChar(index) == ch;
  }

  /**
   * @return True if the next character is equal to ch.
   */
  protected boolean peek(char ch) {
    return peek(ch, 0);
  }

  /**
   * @return The index-th next character in the file ahead of the current position. Returns '\0' if the
   * requested character is past the end of the file.
   */
  protected char peekChar(int index) {
    int offset = index + this.index;
    if (offset >= end) {
      return '\0';
    }
    return file.contents.charAt(offset);
  }

  /**
   * @return The next character in the file.
   */
  protected char peekChar() {
    return peekChar(0);
  }

  /**
   * @return Is this scanner at the end of the file.
   */
  protected boolean atEnd() {
    return index >= end;
  }
}
