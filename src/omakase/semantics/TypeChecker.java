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

import omakase.semantics.symbols.*;
import omakase.syntax.trees.ParseTree;

/**
 */
public class TypeChecker {
  private final Project project;

  public TypeChecker(Project project) {
    this.project = project;
  }

  public void checkAllTypes() {
    for (Symbol symbol : project.getSymbols()) {
      checkSymbol(symbol);
    }
  }

  private void checkSymbol(Symbol symbol) {
    if (symbol.isClass()) {
      checkClassMembers(symbol.asClass());
    } else if (symbol.isFunction()) {
      checkFunction(symbol.asFunction());
    }
  }

  private void checkFunction(FunctionSymbol function) {
    new StatementBinder(new FunctionBindingContext(project, function)).bind(function.tree.body);
  }

  private void checkClassMembers(ClassSymbol clazz) {
    for (Symbol member : clazz.members()) {
      checkMember(member);
    }
  }

  private void checkMember(Symbol member) {
    switch (member.kind) {
    case METHOD:
      checkMethod(member.asMethod());
      break;
    case FIELD:
      checkField(member.asField());
      break;
    default:
      throw new RuntimeException("");
    }
  }

  private void checkField(FieldSymbol member) {
    ParseTree initializer = member.tree.initializer;
    if (initializer != null) {
      new ExpressionBinder(
          new ExpressionBindingContext(
              project,
              project.bindings,
              new IdentifierLookupContext() {
                public Symbol lookupIdentifier(String value) {
                  // TODO: Lookup class members.
                  return null;
                }
              },
              member.isStatic
                  ? null
                  : project.getTypes().getClassType(member.parent)))
          .bind(initializer, member.type);
    }
  }

  private void checkMethod(MethodSymbol member) {
    if (!member.isStatic) {
      new StatementBinder(new MethodBindingContext(project, member)).bind(member.tree.body);
    }
  }

}
