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
import omakase.syntax.trees.*;

/**
 */
public class TypeBinder {
  private final Project project;

  public TypeBinder(Project project) {
    this.project = project;
  }

  private Types types() {
    return project.getTypes();
  }

  public Type bindType(ParseTree type) {
    switch (type.kind) {
    case ARRAY_TYPE:
      return bindArrayType(type.asArrayType());
    case FUNCTION_TYPE:
      return bindFunctionType(type.asFunctionType());
    case KEYWORD_TYPE:
      return bindKeywordType(type.asKeywordType());
    case NAMED_TYPE:
      return bindNamedType(type, type.asNamedType());
    case NULLABLE_TYPE:
      return bindNullableType(type.asNullableType());
    }
    return null;
  }

  private FunctionType bindFunctionType(FunctionTypeTree tree) {
    boolean hadError = false;
    Type returnType = bindType(tree.returnType);
    if (returnType == null) {
      hadError = true;
    }
    ImmutableList<Type> parameterTypes = types().getEmptyTypeArray();
    for (ParseTree parameterTypeTree : tree.argumentTypes) {
      Type parameterType = bindType(parameterTypeTree);
      if (parameterType == null) {
        hadError = true;
      } else {
        parameterTypes = types().getTypeArray(parameterTypes, parameterType);
      }
    }
    return hadError ? null : types().getFunctionType(parameterTypes, returnType);
  }

  private Type bindArrayType(ArrayTypeTree arrayTypeTree) {
    Type elementType = bindType(arrayTypeTree.elementType);
    return elementType == null ? null : types().getArrayType(elementType);
  }

  private Type bindKeywordType(KeywordTypeTree type) {
    return types().getKeywordType(type.type.kind);
  }

  private Type bindNamedType(ParseTree type, NamedTypeTree namedTypeTree) {
    String className = namedTypeTree.name.value;
    ClassSymbol clazz = project.getClass(className);
    if (clazz == null) {
      project.errorReporter().reportError(type.start(), "No class named '%s'.", className);
      return null;
    }
    return types().getClassType(clazz);
  }

  private Type bindNullableType(NullableTypeTree type) {
    Type elementType = bindType(type.elementType);
    return elementType == null ? null : types().getNullableType(elementType);
  }

  public FunctionType bindFunctionType(ParseTree returnTypeTree, FormalParameterListTree formals) {
    boolean hadError = false;
    Type returnType = bindType(returnTypeTree);
    if (returnType == null) {
      hadError = true;
    }
    ImmutableList<Type> parameterTypes = types().getEmptyTypeArray();
    for (ParseTree parameterTree : formals.parameters) {
      Type parameterType = bindType(parameterTree.asParameterDeclaration().type);
      if (parameterType == null) {
        hadError = true;
      } else {
        parameterTypes = types().getTypeArray(parameterTypes, parameterType);
      }
    }
    return hadError ? null : types().getFunctionType(parameterTypes, returnType);
  }
}
