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

package omakase.syntax.tokens.javascript;

import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.util.SourceRange;

/**
 * A token representing a number.
 *
 * NumericLiteralTokens are immutable.
 */
public class NumericLiteralToken extends Token {
  public final int value;

  public NumericLiteralToken(SourceRange range, int value) {
    super(TokenKind.JS_NUMBER, range);
    this.value = value;
  }

  @Override
  public String valueString() {
    return String.format("%d", value);
  }
}