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

package omakase.syntax.trees.javascript;

import omakase.syntax.tokens.javascript.IdentifierToken;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.ParseTreeKind;
import omakase.util.SourceRange;

/**
 */
public class MemberExpressionTree extends ParseTree {
  public final ParseTree object;
  public final IdentifierToken name;

  public MemberExpressionTree(SourceRange location, ParseTree object, IdentifierToken name) {
    super(location, ParseTreeKind.JAVASCRIPT_MEMBER_EXPRESSION);
    this.object = object;
    this.name = name;
  }
}
