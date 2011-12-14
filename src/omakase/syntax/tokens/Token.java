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
 * A token in the omakase language. Tokens represent identifiers, keywords, punctuation or literals.
 * Tokens have a kind and a source range. Some tokens contain additional information which is
 * available in derived classes.
 *
 * Tokens are immutable.
 */
public class Token {
  public final TokenKind kind;
  public final SourceRange location;

  public Token(TokenKind kind, SourceRange location) {
    this.kind = kind;
    this.location = location;
  }

  @Override
  public String toString() {
    return String.format("%s: %s", location, kind.name());
  }
}
