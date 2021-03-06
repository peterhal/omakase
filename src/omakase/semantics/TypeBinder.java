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
import omakase.semantics.symbols.ClassSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.FunctionType;
import omakase.semantics.types.Type;
import omakase.semantics.types.TypeContainer;
import omakase.syntax.trees.*;

/**
 */
public class TypeBinder {
  private final Project project;

  public TypeBinder(Project project) {
    this.project = project;
  }

  private TypeContainer types() {
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
    default:
      throw new RuntimeException("Expected type tree got: " + type.toString());
    }
  }

  private FunctionType bindFunctionType(FunctionTypeTree tree) {
    Type returnType = bindType(tree.returnType);
    ImmutableList<Type> parameterTypes = bindTypeList(tree.argumentTypes);
    return (returnType == null || parameterTypes == null) ? null : types().getFunctionType(parameterTypes, returnType);
  }

  public ImmutableList<Type> bindTypeList(ImmutableList<? extends ParseTree> argumentTrees) {
    boolean hadError = false;
    ImmutableList<Type> parameterTypes = types().getEmptyTypeArray();
    for (ParseTree parameterTypeTree : argumentTrees) {
      Type parameterType = bindType(parameterTypeTree);
      if (parameterType == null) {
        hadError = true;
      } else {
        parameterTypes = types().getTypeArray(parameterTypes, parameterType);
      }
    }
    return hadError ? null : parameterTypes;
  }

  public ImmutableList<Type> bindParameterTypes(ImmutableList<? extends ParameterDeclarationTree> argumentTrees) {
    boolean hadError = false;
    ImmutableList<Type> parameterTypes = types().getEmptyTypeArray();
    for (var parameterTypeTree : argumentTrees) {
      Type parameterType = bindType(parameterTypeTree.type);
      if (parameterType == null) {
        hadError = true;
      } else {
        parameterTypes = types().getTypeArray(parameterTypes, parameterType);
      }
    }
    return hadError ? null : parameterTypes;
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
    Symbol symbol = project.getSymbol(className);
    if (symbol == null) {
      project.errorReporter().reportError(type.start(), "No class named '%s'.", className);
      return null;
    }
    if (symbol.isFunction()) {
      project.errorReporter().reportError(type.start(), "'%s' is a function not a class.", className);
      return null;
    }
    return types().getClassType(symbol.asClass());
  }

  private Type bindNullableType(NullableTypeTree type) {
    Type elementType = bindType(type.elementType);
    return elementType == null ? null : types().getNullableType(elementType);
  }

  public FunctionType bindFunctionType(ParseTree returnTypeTree, FormalParameterListTree formals) {
    Type returnType = bindType(returnTypeTree);
    ImmutableList<Type> parameterTypes = bindParameterTypes(formals.parameters);
    return (returnType == null || parameterTypes == null) ? null : types().getFunctionType(parameterTypes, returnType);
  }
}
