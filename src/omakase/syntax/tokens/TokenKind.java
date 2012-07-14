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
 * The kinds of Tokens. Tokens may be identifiers, keywords, punctuation or literals.
 */
public enum TokenKind {
  IDENTIFIER("identifier"),

  END_OF_FILE("End of file"),
  ERROR("error"),

  // literals
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
  NATIVE("native"),
  NULL("null"),
  TRUE("true"),
  VAR("var"),

  // javascript punctuation
  JAVASCRIPT_OPEN_CURLY("{"),
  JAVASCRIPT_CLOSE_CURLY("}"),
  JAVASCRIPT_OPEN_PAREN("("),
  JAVASCRIPT_CLOSE_PAREN(")"),
  JAVASCRIPT_OPEN_SQUARE("["),
  JAVASCRIPT_CLOSE_SQUARE("]"),
  JAVASCRIPT_PERIOD("."),
  JAVASCRIPT_SEMI_COLON(";"),
  JAVASCRIPT_COMMA(","),
  JAVASCRIPT_OPEN_ANGLE("<"),
  JAVASCRIPT_CLOSE_ANGLE(">"),
  JAVASCRIPT_LESS_EQUAL("<="),
  JAVASCRIPT_GREATER_EQUAL(">="),
  JAVASCRIPT_EQUAL_EQUAL("=="),
  JAVASCRIPT_NOT_EQUAL("!="),
  JAVASCRIPT_EQUAL_EQUAL_EQUAL("==="),
  JAVASCRIPT_NOT_EQUAL_EQUAL("!=="),
  JAVASCRIPT_PLUS("+"),
  JAVASCRIPT_MINUS("-"),
  JAVASCRIPT_STAR("*"),
  JAVASCRIPT_PERCENT("%"),
  JAVASCRIPT_PLUS_PLUS("++"),
  JAVASCRIPT_MINUS_MINUS("--"),
  JAVASCRIPT_SHIFT_LEFT("<<"),
  JAVASCRIPT_SHIFT_RIGHT(">>"),
  JAVASCRIPT_UNSIGNED_SHIFT_RIGHT(">>>"),
  JAVASCRIPT_AMPERSAND("&"),
  JAVASCRIPT_BAR("|"),
  JAVASCRIPT_HAT("^"),
  JAVASCRIPT_BANG("!"),
  JAVASCRIPT_TILDE("~"),
  JAVASCRIPT_AMPERSAND_AMPERSAND("&&"),
  JAVASCRIPT_BAR_BAR("||"),
  JAVASCRIPT_QUESTION("?"),
  JAVASCRIPT_COLON(":"),
  JAVASCRIPT_EQUAL("="),
  JAVASCRIPT_PLUS_EQUAL("+="),
  JAVASCRIPT_MINUS_EQUAL("-="),
  JAVASCRIPT_STAR_EQUAL("*="),
  JAVASCRIPT_PERCENT_EQUAL("%="),
  JAVASCRIPT_LEFT_SHIFT_EQUAL("<<="),
  JAVASCRIPT_RIGHT_SHIFT_EQUAL(">>="),
  JAVASCRIPT_UNSIGNED_RIGHT_SHIFT_EQUAL(">>>="),
  JAVASCRIPT_AMPERSAND_EQUAL("&="),
  JAVASCRIPT_BAR_EQUAL("|="),
  JAVASCRIPT_HAT_EQUAL("^="),
  JAVASCRIPT_SLASH("/"),
  JAVASCRIPT_SLASH_EQUAL("/="),

  // javascript keywords
  JAVASCRIPT_BREAK("break"),
  JAVASCRIPT_CASE("case"),
  JAVASCRIPT_CATCH("catch"),
  JAVASCRIPT_CONTINUE("continue"),
  JAVASCRIPT_DEBUGGER("debugger"),
  JAVASCRIPT_DEFAULT("default"),
  JAVASCRIPT_DELETE("delete"),
  JAVASCRIPT_DO("do"),
  JAVASCRIPT_ELSE("else"),
  JAVASCRIPT_FALSE("false"),
  JAVASCRIPT_FINALLY("finally"),
  JAVASCRIPT_FOR("for"),
  JAVASCRIPT_FUNCTION("function"),
  JAVASCRIPT_IF("if"),
  JAVASCRIPT_IN("in"),
  JAVASCRIPT_INSTANCEOF("instanceof"),
  JAVASCRIPT_NEW("new"),
  JAVASCRIPT_NULL("null"),
  JAVASCRIPT_RETURN("return"),
  JAVASCRIPT_SWITCH("switch"),
  JAVASCRIPT_THIS("this"),
  JAVASCRIPT_THROW("throw"),
  JAVASCRIPT_TRUE("true"),
  JAVASCRIPT_TRY("try"),
  JAVASCRIPT_TYPEOF("typeof"),
  JAVASCRIPT_VAR("var"),
  JAVASCRIPT_VOID("void"),
  JAVASCRIPT_WHILE("while"),
  JAVASCRIPT_WITH("with"),
  ;

  /**
   * Map from string to TokenKind containing all the keywords.
   */
  private static final ImmutableMap<String, TokenKind> keywords;
  static {
    ImmutableMap.Builder<String, TokenKind> map = ImmutableMap.builder();
    for (TokenKind keyword : EnumSet.range(TokenKind.CLASS, TokenKind.VAR)) {
      map.put(keyword.value, keyword);
    }
    keywords = map.build();
  }

  private final String value;

  TokenKind(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  public String value() {
    return value;
  }

  /**
   * Returns the TokenKind of a string if the string is a keyword.
   * Returns null if the value is not a keyword.
   */
  public static TokenKind getKeyword(String value) {
    return keywords.get(value);
  }
}
