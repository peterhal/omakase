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

import com.google.common.collect.ImmutableMap;

import java.util.EnumSet;

/**
 * The kinds of Tokens.
 */
public enum TokenKind {
  IDENTIFIER("identifier"),
  END_OF_FILE("End of file"),
  ERROR("error"),
  NUMBER("number literal"),
  STRING("string literal"),

  // punctuation
  OPEN_CURLY("{"),
  CLOSE_CURLY("}"),
  OPEN_PAREN("("),
  CLOSE_PAREN(")"),
  PERIOD("."),
  SEMI_COLON(";"),
  COMMA(","),
  OPEN_ANGLE("<"),
  CLOSE_ANGLE(">"),
  LESS_EQUAL("<="),
  GREATER_EQUAL(">="),
  EQUAL_EQUAL("=="),
  NOT_EQUAL("!="),
  PLUS("+"),
  MINUS("-"),
  STAR("*"),
  BANG("!"),
  AMPERSAND("&"),
  AMPERSAND_AMPERSAND("&&"),
  BAR("|"),
  BAR_BAR("||"),
  QUESTION("?"),
  COLON(":"),
  EQUAL("="),
  SLASH("/"),

  // keywords
  CLASS("class"),
  FALSE("false"),
  LET("let"),
  NULL("null"),
  TRUE("true"),
  VAR("var"),

  ;

  public final String value;

  TokenKind(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  private static final ImmutableMap<String, TokenKind> keywords;
  static {
    ImmutableMap.Builder<String, TokenKind> map = ImmutableMap.builder();
    for (TokenKind keyword : EnumSet.range(TokenKind.CLASS, TokenKind.VAR)) {
      map.put(keyword.value, keyword);
    }
    keywords = map.build();
  }

  public static TokenKind getKeyword(String value) {
    return keywords.get(value);
  }

}
