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

package omakase.semantics;

import omakase.syntax.trees.FieldDeclarationTree;
import omakase.syntax.trees.MethodDeclarationTree;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.VariableDeclarationTree;

/**
 */
public class ClassMemberDeclarer {
  private final Project project;

  public ClassMemberDeclarer(Project project) {
    this.project = project;
  }

  public void declareMembers() {
    for (ClassSymbol clazz : project.getClasses()) {
      declareClassMembers(clazz);
    }
  }

  private void declareClassMembers(ClassSymbol clazz) {
    for (ParseTree memberTree : clazz.declaration.members) {
      declareMember(clazz, memberTree);
    }
  }

  // TODO: declare member types
  private void declareMember(ClassSymbol clazz, ParseTree tree) {
    if (tree.isMethodDeclaration()) {
      MethodDeclarationTree methodTree = tree.asMethodDeclaration();
      String name = methodTree.name.value;
      if (!checkForDuplicateMember(clazz, name, tree)) {
        new MethodSymbol(clazz, name, methodTree);
      }
    } else {
      FieldDeclarationTree fieldsTree = tree.asFieldDeclaration();
      for (ParseTree t : fieldsTree.declarations) {
        VariableDeclarationTree fieldTree = t.asVariableDeclaration();
        String name = fieldTree.name.value;
        if (!checkForDuplicateMember(clazz, name, t)) {
          new FieldSymbol(clazz, name, fieldTree);
        }
      }
    }
  }

  private boolean checkForDuplicateMember(ClassSymbol clazz, String name, ParseTree tree) {
    Symbol member = clazz.getMember(name);
    if (member != null) {
      project.errorReporter().reportError(tree.location.start, "Duplicate member '%s' in class '%s'.", name, clazz);
      project.errorReporter().reportError(member.location.start(), "Location of duplicate member.");
      return true;
    }
    return false;
  }
}
