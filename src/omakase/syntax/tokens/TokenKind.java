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
  OPEN_SQUARE("["),
  CLOSE_SQUARE("]"),
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
  PLUS_PLUS("++"),
  MINUS_MINUS("--"),
  SHIFT_LEFT("<<"),
  SHIFT_RIGHT(">>"),
  STAR("*"),
  PERCENT("%"),
  BANG("!"),
  TILDE("~"),
  AMPERSAND("&"),
  AMPERSAND_AMPERSAND("&&"),
  BAR("|"),
  BAR_BAR("||"),
  QUESTION("?"),
  COLON(":"),
  EQUAL("="),
  PLUS_EQUAL("+="),
  MINUS_EQUAL("-="),
  STAR_EQUAL("*="),
  PERCENT_EQUAL("%="),
  LEFT_SHIFT_EQUAL("<<="),
  RIGHT_SHIFT_EQUAL(">>="),
  AMPERSAND_EQUAL("&="),
  BAR_EQUAL("|="),
  HAT("^"),
  HAT_EQUAL("^="),
  SLASH("/"),
  SLASH_EQUAL("/="),
  ARROW("->"),

  // keywords
  BREAK("break"),
  CASE("case"),
  CATCH("catch"),
  CLASS("class"),
  CONTINUE("continue"),
  DEBUGGER("debugger"),
  DEFAULT("default"),
  DO("do"),
  ELSE("else"),
  EXTERN("extern"),
  FALSE("false"),
  FINALLY("finally"),
  FOR("for"),
  IF("if"),
  IN("in"),
  INSTANCEOF("instanceof"),
  LET("let"),
  NATIVE("native"),
  NEW("new"),
  NULL("null"),
  RETURN("return"),
  STATIC("static"),
  SWITCH("switch"),
  THIS("this"),
  THROW("throw"),
  TRUE("true"),
  TRY("try"),
  TYPEOF("typeof"),
  VAR("var"),
  VOID("void"),
  WHILE("while"),

  // javascript punctuation
  JS_OPEN_CURLY("{"),
  JS_CLOSE_CURLY("}"),
  JS_OPEN_PAREN("("),
  JS_CLOSE_PAREN(")"),
  JS_OPEN_SQUARE("["),
  JS_CLOSE_SQUARE("]"),
  JS_PERIOD("."),
  JS_SEMI_COLON(";"),
  JS_COMMA(","),
  JS_OPEN_ANGLE("<"),
  JS_CLOSE_ANGLE(">"),
  JS_LESS_EQUAL("<="),
  JS_GREATER_EQUAL(">="),
  JS_EQUAL_EQUAL("=="),
  JS_NOT_EQUAL("!="),
  JS_EQUAL_EQUAL_EQUAL("==="),
  JS_NOT_EQUAL_EQUAL("!=="),
  JS_PLUS("+"),
  JS_MINUS("-"),
  JS_STAR("*"),
  JS_PERCENT("%"),
  JS_PLUS_PLUS("++"),
  JS_MINUS_MINUS("--"),
  JS_SHIFT_LEFT("<<"),
  JS_SHIFT_RIGHT(">>"),
  JS_UNSIGNED_SHIFT_RIGHT(">>>"),
  JS_AMPERSAND("&"),
  JS_BAR("|"),
  JS_HAT("^"),
  JS_BANG("!"),
  JS_TILDE("~"),
  JS_AMPERSAND_AMPERSAND("&&"),
  JS_BAR_BAR("||"),
  JS_QUESTION("?"),
  JS_COLON(":"),
  JS_EQUAL("="),
  JS_PLUS_EQUAL("+="),
  JS_MINUS_EQUAL("-="),
  JS_STAR_EQUAL("*="),
  JS_PERCENT_EQUAL("%="),
  JS_LEFT_SHIFT_EQUAL("<<="),
  JS_RIGHT_SHIFT_EQUAL(">>="),
  JS_UNSIGNED_RIGHT_SHIFT_EQUAL(">>>="),
  JS_AMPERSAND_EQUAL("&="),
  JS_BAR_EQUAL("|="),
  JS_HAT_EQUAL("^="),
  JS_SLASH("/"),
  JS_SLASH_EQUAL("/="),

  // javascript keywords
  JS_BREAK("break"),
  JS_CASE("case"),
  JS_CATCH("catch"),
  JS_CONTINUE("continue"),
  JS_DEBUGGER("debugger"),
  JS_DEFAULT("default"),
  JS_DELETE("delete"),
  JS_DO("do"),
  JS_ELSE("else"),
  JS_FALSE("false"),
  JS_FINALLY("finally"),
  JS_FOR("for"),
  JS_FUNCTION("function"),
  JS_IF("if"),
  JS_IN("in"),
  JS_INSTANCEOF("instanceof"),
  JS_NEW("new"),
  JS_NULL("null"),
  JS_RETURN("return"),
  JS_SWITCH("switch"),
  JS_THIS("this"),
  JS_THROW("throw"),
  JS_TRUE("true"),
  JS_TRY("try"),
  JS_TYPEOF("typeof"),
  JS_VAR("var"),
  JS_VOID("void"),
  JS_WHILE("while"),
  JS_WITH("with"),

  // Misc javascript tokens
  JS_IDENTIFIER("identifier"),
  JS_END_OF_FILE("End of file"),
  JS_ERROR("error"),
  JS_NUMBER("number literal"),
  JS_STRING("string literal"),
  ;

  /**
   * Map from string to TokenKind containing all the keywords.
   */
  private static final ImmutableMap<String, TokenKind> keywords;

  /**
   * Map from string to TokenKind containing all the keywords.
   */
  private static final ImmutableMap<String, TokenKind> javascriptKeywords;

  static {
    keywords = buildKeywords(TokenKind.BREAK, TokenKind.WHILE);
    javascriptKeywords = buildKeywords(TokenKind.JS_BREAK, TokenKind.JS_WITH);
  }

  private static ImmutableMap<String, TokenKind> buildKeywords(TokenKind start, TokenKind end) {
    ImmutableMap.Builder<String, TokenKind> map = ImmutableMap.builder();
    for (TokenKind keyword : EnumSet.range(start, end)) {
      map.put(keyword.value, keyword);
    }
    return map.build();
  }

  private final String value;

  TokenKind(String value) {
    this.value = value;
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

  /**
   * Returns the TokenKind of a string if the string is a keyword.
   * Returns null if the value is not a keyword.
   */
  public static TokenKind getJavascriptKeyword(String value) {
    return javascriptKeywords.get(value);
  }
}
