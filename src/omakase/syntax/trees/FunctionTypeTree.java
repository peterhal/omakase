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
 */
public class FunctionTypeTree extends ParseTree {
  public final ImmutableList<? extends ParseTree> argumentTypes;
  public final ParseTree returnType;

  public FunctionTypeTree(SourceRange location, ImmutableList<? extends ParseTree> argumentTypes, ParseTree returnType) {
    super(location, ParseTreeKind.FUNCTION_TYPE);
    this.argumentTypes = argumentTypes;
    this.returnType = returnType;
  }
}
