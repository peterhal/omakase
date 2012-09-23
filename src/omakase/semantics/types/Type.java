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
public class Type {
  public final TypeKind kind;

  public Type(TypeKind kind) {
    this.kind = kind;
  }

  public boolean isBoolType() {
    return isKeywordType(TokenKind.BOOL);
  }

  public boolean isDynamicType() {
    return isKeywordType(TokenKind.DYNAMIC);
  }

  public boolean isNumberType() {
    return isKeywordType(TokenKind.NUMBER);
  }

  public boolean isStringType() {
    return isKeywordType(TokenKind.STRING);
  }

  public boolean isNullType() {
    return isKeywordType(TokenKind.NULL);
  }

  public boolean isVoidType() {
    return isKeywordType(TokenKind.VOID);
  }

  private boolean isKeywordType(TokenKind keyword) {
    return isKeywordType() && this.asKeywordType().keyword == keyword;
  }

  public boolean isKeywordType() {
    return kind == TypeKind.KEYWORD;
  }

  public boolean isClassType() {
    return kind == TypeKind.CLASS;
  }

  public boolean isArrayType() {
    return kind == TypeKind.ARRAY;
  }

  public boolean isNullableType() {
    return kind == TypeKind.NULLABLE;
  }

  public KeywordType asKeywordType() {
    return (KeywordType) this;
  }

  public ArrayType asArrayType() {
    return (ArrayType) this;
  }

  public NullableType asNullableType() {
    return (NullableType) this;
  }
}
