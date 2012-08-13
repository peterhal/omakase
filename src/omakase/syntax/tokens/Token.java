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

import omakase.util.SourceLocation;
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
    return String.format("%s: %s", location, kind.name())
      + (hasValue() ? " " + valueString() : "");
  }

  public String toValueString() {
    if (hasValue()) {
      return valueString();
    } else {
      return kind.name();
    }
  }
  
  public boolean hasValue() {
    switch (kind) {
    case IDENTIFIER:
    case STRING:
    case NUMBER:
    case JS_IDENTIFIER:
    case JS_STRING:
    case JS_NUMBER:
      return true;
    default:
      return false;
    }
  }
  
  public String valueString() {
    throw new RuntimeException("No value string.");
  }

  public SourceLocation start() {
    return this.location.start;
  }

  public SourceLocation end() {
    return this.location.end;
  }

  public omakase.syntax.tokens.IdentifierToken asIdentifier() {
    return (omakase.syntax.tokens.IdentifierToken) this;
  }

  public omakase.syntax.tokens.NumericLiteralToken asNumericLiteral() {
    return (omakase.syntax.tokens.NumericLiteralToken) this;
  }

  public omakase.syntax.tokens.StringLiteralToken asStringLiteral() {
    return (omakase.syntax.tokens.StringLiteralToken) this;
  }

  public omakase.syntax.tokens.javascript.IdentifierToken asJavascriptIdentifier() {
    return (omakase.syntax.tokens.javascript.IdentifierToken) this;
  }

  public boolean isJavascriptIdentifier() {
    return kind == TokenKind.JS_IDENTIFIER;
  }

  public omakase.syntax.tokens.javascript.NumericLiteralToken asJavascriptNumericLiteral() {
    return (omakase.syntax.tokens.javascript.NumericLiteralToken) this;
  }

  public omakase.syntax.tokens.javascript.StringLiteralToken asJavascriptStringLiteral() {
    return (omakase.syntax.tokens.javascript.StringLiteralToken) this;
  }
}
