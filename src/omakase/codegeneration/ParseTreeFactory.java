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
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.javascript.*;

/**
 */
public final class ParseTreeFactory {
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
    return new FunctionExpressionTree(null, parameters, body);
  }
}
