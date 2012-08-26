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

import java.util.ArrayList;
import java.util.List;

import static omakase.codegeneration.JavascriptParseTreeFactory.*;

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
      ParseTree constructor = createConstructor(tree);
      List<ParseTree> members = createMembers(tree);
      members.add(0, constructor);
      return createParenExpression(createCall(
          createFunction(createFormalParameterList(), createBlock(members))
      ));
    }

    private ParseTree createConstructor(ClassDeclarationTree tree) {
      return createAssignmentStatement(
          createIdentifier(tree.name),
          createFunction(createFormalParameterList(), createBlock())
      );
    }

    private List<ParseTree> createMembers(ClassDeclarationTree tree) {
      ArrayList<ParseTree> result = new ArrayList<ParseTree>();
      for (ParseTree member: tree.members) {
        result.add(createMember(tree.name.value, member.asMethodDeclaration()));
      }
      return result;
    }

    private ParseTree createMember(String className, MethodDeclarationTree method) {
      //    C.prototype.member = ...;
      return createAssignmentStatement(
          createDottedName(className, "prototype", method.name.value),
          createFunction(createFormalParameterList(), createBlock()));
    }
  }
}
