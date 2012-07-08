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

import omakase.syntax.ParseTreeTransformer;
import omakase.syntax.trees.*;
import omakase.syntax.trees.javascript.ProgramTree;

import static omakase.codegeneration.ParseTreeFactory.*;

/**
 *
 */
public class JavascriptTransformer extends ParseTreeTransformer {
  @Override
  protected ParseTree transform(SourceFileTree tree) {
    return new ProgramTree(null, transformList(tree.declarations));
  }

  @Override
  protected ParseTree transform(ClassDeclarationTree tree) {
    return new ClassTransformer().transform(tree);
  }

    private static class ClassTransformer {
      // class C { members } =>
      //
      //  (function() {
      //    C = function() {}
      //    C.prototype.member = ...;
      //  }());
      //
      public ParseTree transform(ClassDeclarationTree tree) {
        return createParenExpression(createCall(
            createFunction()
        ));
      }
    }
}
