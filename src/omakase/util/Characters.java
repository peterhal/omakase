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

public class Characters {
  /**
   * @return True if ch may start an identifier.
   */
  public static boolean isIdentifierStartChar(char ch) {
    return Character.isLetter(ch) || ch == '_' || ch == '$';
  }

  /**
   * @return True if ch is valid in an identifier.
   */
  public static boolean isIdentifierPartChar(char ch) {
    return isIdentifierStartChar(ch) || isDigit(ch);
  }

  /**
   * @return True if ch is a hexadecimal digit.
   */
  public static boolean isHexDigit(char ch) {
    switch (ch) {
    case '0':
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
    case 'a':
    case 'b':
    case 'c':
    case 'd':
    case 'e':
    case 'f':
    case 'A':
    case 'B':
    case 'C':
    case 'D':
    case 'E':
    case 'F':
      return true;
    default:
      return false;
    }
  }

  /**
   * @return Converts a hexadecimal digit character to its numeric value.
   */
  public static int hexDigitToValue(char ch) {
    switch (ch) {
    case '0':
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
      return digitToValue(ch);
    case 'a':
    case 'b':
    case 'c':
    case 'd':
    case 'e':
    case 'f':
      return 10 + (ch - 'a');
    case 'A':
    case 'B':
    case 'C':
    case 'D':
    case 'E':
    case 'F':
      return 10 + (ch - 'A');
    default:
      throw new RuntimeException();
    }
  }

  /**
   * @return Converts a decimal digit character to its numeric value.
   */
  public static int digitToValue(char digit) {
    return digit - '0';
  }

  /**
   * @return Returns true if ch is a decimal digit character.
   */
  public static boolean isDigit(char ch) {
    switch (ch) {
    case '0':
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
      return true;
    default:
      return false;
    }
  }

  /**
   * @return Returns true if ch is a line terminator character.
   */
  public static boolean isLineTerminator(char ch) {
    switch (ch) {
    case '\n':
    case '\r':
    case '\u2028':
    case '\u2029':
      return true;
    default:
      return false;
    }
  }
}