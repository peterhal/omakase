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

import com.google.common.collect.ImmutableMap;
import omakase.semantics.symbols.*;
import omakase.semantics.types.FunctionType;
import omakase.semantics.types.Type;
import omakase.syntax.trees.*;

import java.util.HashMap;
import java.util.Map;

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

  private void declareMember(ClassSymbol clazz, ParseTree tree) {
    if (tree.isMethodDeclaration()) {
      declareMethod(clazz, tree.asMethodDeclaration());
    } else {
      FieldDeclarationTree fieldsTree = tree.asFieldDeclaration();
      for (VariableDeclarationTree fieldTree : fieldsTree.declarations) {
        declareField(clazz, fieldTree);
      }
    }
  }

  private void declareMethod(ClassSymbol clazz, MethodDeclarationTree methodTree) {
    String name = methodTree.name.value;
    FunctionType type = bindMethodType(methodTree);
    Map<String, ParameterSymbol> parameters = buildParameters(methodTree.formals);
    if (!checkForDuplicateMember(clazz, name, methodTree)) {
      new MethodSymbol(clazz, methodTree, type, parameters);
    }
  }

  private Map<String, ParameterSymbol> buildParameters(FormalParameterListTree formals) {
    Map<String, ParameterSymbol> parameters = new HashMap<String, ParameterSymbol>();
    for (ParameterDeclarationTree tree : formals.parameters) {
      String name = tree.name.value;
      Type type = null;
      if (tree.type != null) {
        type = bindType(tree.type);
      }
      if (parameters.containsKey(name)) {
        reportError(tree, "Duplicate parameter '%s'.", name);
      } else {
        ParameterSymbol parameter = new ParameterSymbol(tree, type);
        parameters.put(name, parameter);
      }
    }
    return ImmutableMap.copyOf(parameters);
  }

  private void reportError(ParseTree tree, String message, Object... args) {
    project.errorReporter().reportError(tree.location.start, message, args);
  }

  private FunctionType bindMethodType(MethodDeclarationTree methodTree) {
    return new TypeBinder(project).bindFunctionType(methodTree.returnType, methodTree.formals);
  }

  private void declareField(ClassSymbol clazz, VariableDeclarationTree fieldTree) {
    String name = fieldTree.name.value;
    Type type = bindType(fieldTree.type);
    if (!checkForDuplicateMember(clazz, name, fieldTree)) {
      new FieldSymbol(clazz, name, fieldTree, type);
    }
  }

  private Type bindType(ParseTree type) {
    return new TypeBinder(project).bindType(type);
  }

  private TypeContainer types() {
    return project.getTypes();
  }

  private boolean checkForDuplicateMember(ClassSymbol clazz, String name, ParseTree tree) {
    Symbol member = clazz.getMember(name);
    if (member != null) {
      reportError(tree, "Duplicate member '%s' in class '%s'.", name, clazz);
      reportError(member.location, "Location of duplicate member.");
      return true;
    }
    return false;
  }
}
