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

import omakase.syntax.trees.ClassDeclarationTree;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class ClassSymbol extends Symbol {
  public final ClassDeclarationTree declaration;
  private final Map<String, Symbol> members;

  public ClassSymbol(String name, ClassDeclarationTree declaration) {
    super(SymbolKind.CLASS, name, declaration);
    this.declaration = declaration;
    this.members = new LinkedHashMap<String, Symbol>();
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
}
