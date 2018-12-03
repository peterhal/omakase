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

import omakase.syntax.tokens.TokenKind;

/**
 * Generates identifiers. Each generated identifier is as short as possible and unique.
 */
public class IdentifierGenerator {
  private StringBuilder current = new StringBuilder();

  public String next() {
    var current = nextInternal();
    while (isKeyword(current)) {
      current = nextInternal();
    }
    return current;
  }

  private static boolean isKeyword(String value) {
    return TokenKind.getJavascriptKeyword(value) != null;
  }

  /**
   * Increments the current string. Returns a valid identifier-or-keyword.
   */
  private String nextInternal() {
    int index = 0;
    while (index < current.length()) {
      if (incrementChar(index)) {
        return current.toString();
      }
      index += 1;
    }
    current.append('a');
    return current.toString();
  }

  /**
   * Increments the char at index. index must be a valid index into current.
   * Returns true if the incrementChar did not wrap, returns false if it did wrap.
   */
  private boolean incrementChar(int index) {
    if (index == 0) {
      return incrementIdentifierStartChar(index);
    } else {
      if (current.charAt(index) == '9') {
        current.setCharAt(index, 'a');
        return false;
      } else if (!incrementIdentifierStartChar(index)) {
        current.setCharAt(index, '0');
        return true;
      } else {
        return true;
      }
    }
  }

  /**
   * Digits are not valid as identifier start chars.
   */
  private boolean incrementIdentifierStartChar(int index) {
    var ch = current.charAt(index);
    switch (ch) {
    case 'z':
      current.setCharAt(index, 'A');
      break;
    case 'Z':
      current.setCharAt(index, '$');
      break;
    case '$':
      current.setCharAt(index, '_');
      break;
    case '_':
      current.setCharAt(index, 'a');
      return false;
    default:
      current.setCharAt(index, (char) (ch + 1));
      break;
    }
    return true;
  }
}
