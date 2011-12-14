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

package omakase.syntax.tokens;

import omakase.util.SourceRange;

/**
 * A token representing a quoted string.
 *
 * StringLiteralTokens are immutable.
 */
public class StringLiteralToken extends Token {
  public final String value;

  public StringLiteralToken(SourceRange range, String value) {
    super(TokenKind.STRING, range);
    this.value = value;
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" \"%s\"", value);
  }
}
