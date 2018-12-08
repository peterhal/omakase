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

import com.google.common.collect.ImmutableList;
import omakase.semantics.symbols.*;
import omakase.semantics.types.FunctionType;
import omakase.semantics.types.Type;
import omakase.syntax.trees.*;

import java.util.Map;

/**
 */
public class ClassMemberDeclarer extends ParameterDeclarer {
  public ClassMemberDeclarer(Project project) {
    super(project);
  }

  public void declareMembers() {
    for (Symbol symbol : project.getSymbols()) {
      if (symbol.isClass()) {
        declareClassMembers(symbol.asClass());
      }
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
      for (ParseTree fieldTree : fieldsTree.declarations) {
        declareField(clazz, (VariableDeclarationTree) fieldTree, fieldsTree.isStatic);
      }
    }
  }

  private void declareMethod(ClassSymbol clazz, MethodDeclarationTree methodTree) {
    String name = methodTree.name.value;
    FunctionType type = bindMethodType(methodTree);
    Map<String, ParameterSymbol> parameters = buildParameters(methodTree.formals);
    if (!checkForDuplicateMember(clazz, name, methodTree) && type != null) {
      var symbol = new MethodSymbol(clazz, methodTree, type, parameters);
      project.bindings.setSymbol(methodTree, symbol);
    }
  }

  private FunctionType bindMethodType(MethodDeclarationTree methodTree) {
    if (isConstructor(methodTree)) {
      ImmutableList<Type> parameterTypes = new TypeBinder(project).bindParameterTypes(methodTree.formals.parameters);
      if (parameterTypes == null) {
        if (!project.errorReporter().hadError()) {
          throw new RuntimeException("expected to have had error");
        }
        return null;
      }
      return project.getTypes().getFunctionType(parameterTypes, project.getTypes().getVoidType());
    } else {
      return new TypeBinder(project).bindFunctionType(methodTree.returnType, methodTree.formals);
    }
  }

  private static boolean isConstructor(MethodDeclarationTree tree) {
    return tree.returnType == null;
  }

  private void declareField(ClassSymbol clazz, VariableDeclarationTree fieldTree, boolean isStatic) {
    String name = fieldTree.name.value;
    Type type = bindType(fieldTree.type);
    if (!checkForDuplicateMember(clazz, name, fieldTree)) {
      var symbol = new FieldSymbol(clazz, name, fieldTree, type, isStatic);
      project.bindings.setSymbol(fieldTree, symbol);
    }
  }
}
