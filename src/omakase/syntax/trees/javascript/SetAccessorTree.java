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

import omakase.syntax.tokens.IdentifierToken;
import omakase.syntax.tokens.Token;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.ParseTreeKind;
import omakase.util.SourceRange;

/**
 */
public class SetAccessorTree extends ParseTree {
  public final Token propertyName;
  public final IdentifierToken parameterName;
  public final BlockTree body;

  public SetAccessorTree(SourceRange location, Token propertyName, IdentifierToken parameterName, BlockTree body) {
    super(location, ParseTreeKind.JAVASCRIPT_SET_ACCESSOR);
    this.propertyName = propertyName;
    this.parameterName = parameterName;
    this.body = body;
  }
}
