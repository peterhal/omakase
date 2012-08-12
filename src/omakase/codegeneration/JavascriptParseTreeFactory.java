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

package omakase.codegeneration;

import com.google.common.collect.ImmutableList;
import omakase.syntax.tokens.Token;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.tokens.javascript.IdentifierToken;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.javascript.*;

import java.util.List;

/**
 */
public final class JavascriptParseTreeFactory {
  public static ParseTree createCall(ParseTree function, ParseTree... arguments) {
    return new CallExpressionTree(null, function, createList(arguments));
  }
  
  public static ParseTree createParenExpression(ParseTree expression) {
    return new ParenExpressionTree(null, expression);
  }
  
  public static ImmutableList<ParseTree> createList(ParseTree... arguments) {
    return ImmutableList.copyOf(arguments);
  }
  
  public static ParseTree createFunction(FormalParameterListTree parameters, BlockTree body) {
    return new FunctionExpressionTree(null, null, parameters, body);
  }
  
  public static FormalParameterListTree createFormalParameterList() {
    return new FormalParameterListTree(null, ImmutableList.<IdentifierToken>of());
  }

  public static BlockTree createBlock(ParseTree... statements) {
    return createBlock(ImmutableList.<ParseTree>copyOf(statements));
  }

  public static BlockTree createBlock(ImmutableList<ParseTree> statements) {
    return new BlockTree(null, statements);
  }

  public static BlockTree createBlock(List<ParseTree> statements) {
    return new BlockTree(null, ImmutableList.<ParseTree>copyOf(statements));
  }

  public static ParseTree createDottedName(String... names) {
    ParseTree result = null;
    for (String name: names) {
      if (result == null) {
        result = createIdentifier(name);
      } else {
        result = new MemberExpressionTree(null, result, new omakase.syntax.tokens.IdentifierToken(null, name));
      }
    }
    return result;
  }

  public static ParseTree createAssignmentStatement(ParseTree left, ParseTree right) {
    return createExpressionStatement(
        createBinaryExpression(left, TokenKind.JS_EQUAL, right)
    );
  }

  private static ParseTree createIdentifier(String value) {
    return createIdentifier(new IdentifierToken(null, value));
  }

  public static ParseTree createIdentifier(omakase.syntax.tokens.IdentifierToken identifier) {
    return createIdentifier(identifier.value);
  }

  public static ParseTree createIdentifier(IdentifierToken identifier) {
    return new IdentifierExpressionTree(null, identifier);
  }

  public static ParseTree createBinaryExpression(ParseTree left, TokenKind kind, ParseTree right) {
    return new BinaryExpressionTree(null, left, createToken(kind), right);
  }

  public static Token createToken(TokenKind kind) {
    return new Token(kind, null);
  }

  public static ParseTree createExpressionStatement(ParseTree expression) {
    return new ExpressionStatementTree(null, expression);
  }
}
