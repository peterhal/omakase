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

package omakase.semantics.types;

import com.google.common.collect.ImmutableList;
import omakase.semantics.symbols.ClassSymbol;
import omakase.semantics.types.*;
import omakase.syntax.tokens.TokenKind;
import omakase.syntax.trees.FunctionExpressionTree;

import java.util.HashMap;
import java.util.Map;

/**
 * Container of all type information for a project.
 */
public class TypeContainer {
  private final Map<TokenKind, KeywordType> keywordTypes;
  private final Map<ClassSymbol, ClassType> classTypes;
  private final Map<Type, ArrayType> arrayTypes;
  private final Map<Type, NullableType> nullableTypes;
  private final ImmutableList<Type> emptyTypeArray;
  private final Map<ImmutableList<Type>, Map<Type, ImmutableList<Type>>> typeArrays;
  private final Map<ImmutableList<Type>, Map<Type, FunctionType>> functionTypes;
  private final Map<FunctionExpressionTree, UnboundFunctionLiteralType> unboundFunctionLiterals;

  public TypeContainer() {
    this.keywordTypes = new HashMap<TokenKind, KeywordType>();
    addKeywordType(TokenKind.BOOL);
    addKeywordType(TokenKind.DYNAMIC);
    addKeywordType(TokenKind.NULL);
    addKeywordType(TokenKind.NUMBER);
    addKeywordType(TokenKind.OBJECT);
    addKeywordType(TokenKind.STRING);
    addKeywordType(TokenKind.VOID);
    addKeywordType(TokenKind.CLASS); // Kinda hacky -

    this.classTypes = new HashMap<ClassSymbol, ClassType>();

    this.arrayTypes = new HashMap<Type, ArrayType>();

    this.nullableTypes = new HashMap<Type, NullableType>();

    this.emptyTypeArray = ImmutableList.of();
    this.typeArrays = new HashMap<ImmutableList<Type>, Map<Type, ImmutableList<Type>>>();

    this.functionTypes = new HashMap<ImmutableList<Type>, Map<Type, FunctionType>>();

    this.unboundFunctionLiterals = new HashMap<FunctionExpressionTree, UnboundFunctionLiteralType>();
  }

  private void addKeywordType(TokenKind kind) {
    this.keywordTypes.put(kind, new KeywordType(kind));
  }

  public void addClassType(ClassSymbol classSymbol) {
    classTypes.put(classSymbol, new ClassType(classSymbol));
  }

  public KeywordType getKeywordType(TokenKind kind) {
    return keywordTypes.get(kind);
  }

  public ArrayType getArrayType(Type elementType) {
    ArrayType result = arrayTypes.get(elementType);
    if (result == null) {
      result = new ArrayType(elementType);
      arrayTypes.put(elementType, result);
    }
    return result;
  }

  public NullableType getNullableType(Type elementType) {
    NullableType result = nullableTypes.get(elementType);
    if (result == null) {
      result = new NullableType(elementType);
      nullableTypes.put(elementType, result);
    }
    return result;
  }

  public ClassType getClassType(ClassSymbol classSymbol) {
    return classTypes.get(classSymbol);
  }

  public ImmutableList<Type> getEmptyTypeArray() {
    return emptyTypeArray;
  }

  public ImmutableList<Type> getTypeArray(ImmutableList<Type> head, Type tail) {
    Map<Type, ImmutableList<Type>> map = typeArrays.get(head);
    if (map == null) {
      map = new HashMap<Type, ImmutableList<Type>>();
      typeArrays.put(head, map);
    }

    ImmutableList<Type> result = map.get(tail);
    if (result == null) {
      ImmutableList.Builder<Type> builder = new ImmutableList.Builder<Type>();
      for (Type element : head) {
        builder.add(element);
      }
      result = builder.build();
      map.put(tail, result);
    }

    return result;
  }

  public FunctionType getFunctionType(ImmutableList<Type> parameterTypes, Type returnType) {
    Map<Type, FunctionType> map = functionTypes.get(parameterTypes);
    if (map == null) {
      map = new HashMap<Type, FunctionType>();
      functionTypes.put(parameterTypes, map);
    }

    FunctionType result = map.get(returnType);
    if (result == null) {
      result = new FunctionType(returnType, parameterTypes);
      map.put(returnType, result);
    }

    return result;
  }

  public KeywordType getBoolType() {
    return getKeywordType(TokenKind.BOOL);
  }

  // Internal only - used for typing 'null' literal expressions.
  public KeywordType getNullType() {
    return getKeywordType(TokenKind.NULL);
  }

  public KeywordType getStringType() {
    return getKeywordType(TokenKind.STRING);
  }

  public KeywordType getNumberType() {
    return getKeywordType(TokenKind.NUMBER);
  }

  public KeywordType getDynamicType() {
    return getKeywordType(TokenKind.DYNAMIC);
  }

  public KeywordType getVoidType() {
    return getKeywordType(TokenKind.VOID);
  }

  public UnboundFunctionLiteralType getUnboundFunctionLiteralType(FunctionExpressionTree tree) {
    UnboundFunctionLiteralType result = unboundFunctionLiterals.get(tree);
    if (result == null) {
      result = new UnboundFunctionLiteralType(tree);
      unboundFunctionLiterals.put(tree, result);
    }
    return result;
  }

  // The type of an expression which names a class.
  public Type getClassSymbolType() {
    return getKeywordType(TokenKind.CLASS);
  }
}
