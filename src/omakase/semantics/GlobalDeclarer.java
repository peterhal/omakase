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
import omakase.semantics.symbols.FunctionSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.semantics.symbols.TypeVariableSymbol;
import omakase.syntax.tokens.IdentifierToken;
import omakase.syntax.tokens.Token;
import omakase.syntax.trees.*;
import omakase.util.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class GlobalDeclarer extends ParameterDeclarer {
  public GlobalDeclarer(Project project) {
    super(project);
  }

  public static void declare(Project project) {
    new GlobalDeclarer(project).declare();
  }

  public void declare() {
    // Declare classes first, as we need classes for function types
    for (SourceFileTree tree : project.trees()) {
      declareClasses(tree);
    }

    for (SourceFileTree tree : project.trees()) {
      declareFunctions(tree);
    }
  }

  private void declareFunctions(SourceFileTree tree) {
    for (ParseTree element : tree.declarations) {
      if (element.isFunctionDeclaration()) {
        FunctionDeclarationTree functionDeclaration = element.asFunctionDeclaration();
        String functionName = functionDeclaration.name.value;
        if (checkDuplicateSymbol(project.getSymbolMap(), functionName, functionDeclaration.name)) return;

        var type = new TypeBinder(project).bindFunctionType(functionDeclaration.returnType, functionDeclaration.formals);
        project.addFunction(new FunctionSymbol(functionDeclaration, type, buildParameters(functionDeclaration.formals, functionDeclaration.isJavascript)));
      }
    }
  }

  private void declareClasses(SourceFileTree tree) {
    for (ParseTree element : tree.declarations) {
      if (element.isClassDeclaration()) {
        ClassDeclarationTree classDeclaration = element.asClassDeclaration();
        String className = classDeclaration.name.value;
        var typeParameters = declareTypeParameters(classDeclaration.typeParameters);
        if (checkDuplicateSymbol(project.getSymbolMap(), className, classDeclaration.name)) return;
        project.addClass(new ClassSymbol(
            className,
            classDeclaration,
            project.getTypes().getClassSymbolType(),
            typeParameters));
      }
    }
  }

  private HashMap<String, TypeVariableSymbol> declareTypeParameters(List<? extends ParseTree> typeParameters) {
    var result = new HashMap<String, TypeVariableSymbol>();
    if (typeParameters != null) {
      for (var typeParameter: typeParameters) {
        declareTypeParameter(result, typeParameter.asTypeParameterDeclaration());
      }
    }
    return result;
  }

  private void declareTypeParameter(HashMap<String, TypeVariableSymbol> typeParameters, TypeParameterDeclarationTree tree) {
    var name = tree.name;
    if (checkDuplicateSymbol(typeParameters, name.value, name)) {
      return;
    }
    typeParameters.put(name.value, new TypeVariableSymbol(tree, name));
  }

  private boolean checkDuplicateSymbol(Map<String, ? extends Symbol> symbolMap, String name, IdentifierToken tree) {
    if (symbolMap.containsKey(name)) {
      reportError(tree, "Duplicate class '%s'.", name);
      reportRelatedError(symbolMap.get(name));
      return true;
    }
    return false;
  }

  private void reportRelatedError(Symbol symbol) {
    reportError(symbol.location.start(), "Related location");
  }

  private void reportError(Token token, String format, Object... args) {
    reportError(token.start(), format, args);
  }

  private void reportError(SourceLocation location, String format, Object... args) {
    project.errorReporter().reportError(location, format, args);
  }
}
