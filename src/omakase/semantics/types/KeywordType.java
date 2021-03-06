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

package omakase.semantics.types;

import omakase.syntax.tokens.TokenKind;

/**
 */
public class KeywordType extends Type {
  public final TokenKind keyword;

  public KeywordType(TokenKind keyword) {
    super(TypeKind.KEYWORD);
    this.keyword = keyword;
  }

  @Override
  protected String computeDisplayName() {
    return keyword.value();
  }
}
