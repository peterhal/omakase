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

package omakase.syntax.trees;

import com.google.common.collect.ImmutableList;
import omakase.util.SourceRange;

/**
 *
 */
public class FormalParameterListTree extends ParseTree {
  public final ImmutableList<? extends ParameterDeclarationTree> parameters;

  public FormalParameterListTree(SourceRange location, ImmutableList<? extends ParameterDeclarationTree> parameters) {
    super(location, ParseTreeKind.FORMAL_PARAMETER_LIST);
    this.parameters = parameters;
  }

  public boolean hasUntypedParameters() {
    return size() > 0 && parameters.get(0).type == null;
  }

  public int size() {
    return parameters.size();
  }
}
