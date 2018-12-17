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

package omakase.semantics.symbols;

import com.google.common.collect.ImmutableList;
import omakase.semantics.types.Type;
import omakase.syntax.trees.ClassDeclarationTree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ClassSymbol extends Symbol {
  public final ClassDeclarationTree declaration;
  private final Type type;
  // TODO: Static and instance members with same name.
  private final Map<String, Symbol> members;
  private final Map<String, TypeVariableSymbol> typeParameters;

  public ClassSymbol(String name, ClassDeclarationTree declaration, Type type, Map<String, TypeVariableSymbol> typeParameters) {
    super(SymbolKind.CLASS, name, declaration);
    this.declaration = declaration;
    this.typeParameters = typeParameters;
    this.type = type;
    this.members = new LinkedHashMap<>();
  }

  public void addMember(Symbol member) {
    members.put(member.name, member);
  }

  public Iterable<Symbol> members() {
    return members.values();
  }

  public Symbol getMember(String name) {
    return members.get(name);
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public boolean isWritable() {
    return false;
  }

  public Symbol lookupMember(String name, boolean isStatic) {
    // TODO: isStatic
    return members.get(name);
  }

  public boolean isExtern() {
    return declaration.isExtern;
  }
}
