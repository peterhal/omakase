// Copyright 2018 Peter Hallam
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

package omakase.codegeneration;

import omakase.printtree.ParseTreeWriter;
import omakase.semantics.Project;
import omakase.semantics.symbols.ClassSymbol;
import omakase.semantics.symbols.FunctionSymbol;
import omakase.semantics.symbols.Symbol;
import omakase.syntax.trees.IdentifierExpressionTree;
import omakase.syntax.trees.MemberExpressionTree;
import omakase.syntax.trees.ParseTree;

import java.util.HashMap;
import java.util.Map;

import static omakase.codegeneration.JavascriptParseTreeFactory.createIdentifier;
import static omakase.codegeneration.JavascriptParseTreeFactory.createMemberExpression;

public class OptimizedJavascriptTransformer extends JavascriptTransformer {
  private final Project project;
  private final IdentifierGenerator idGenerator = new IdentifierGenerator();
  private final Map<Symbol, String> symbolNames = new HashMap<>();

  public OptimizedJavascriptTransformer(Project project) {
    this.project = project;
  }


  public void write(ParseTreeWriter writer) {
    for (var symbol: project.getSymbols()) {
      if (symbol.isClass()) {
        writeClass(writer, symbol.asClass());
      } else if (symbol.isFunction()) {
        writeFunction(writer, symbol.asFunction());
      } else {
        throw new RuntimeException("Unexpected symbol");
      }
    }
  }

  private void writeFunction(ParseTreeWriter writer, FunctionSymbol function) {
    writer.visitAny(transformFunction(getSymbolName(function), function.tree));
  }

  private String getSymbolName(Symbol symbol) {
    if (symbol.isExtern()) {
      return symbol.name;
    }

    if (!symbolNames.containsKey(symbol)) {
      symbolNames.put(symbol, idGenerator.next());
    }
    return symbolNames.get(symbol);
  }

  private class OptimizedClassTransformer extends ClassTransformer {
    private final ClassSymbol clazz;
    private final String name;

    public OptimizedClassTransformer(ClassSymbol clazz) {
      super(clazz.declaration);
      this.clazz = clazz;
      if (clazz.isExtern()) {
        this.name = clazz.name;
      } else {
        this.name = getSymbolName(clazz);
      }
    }

    @Override
    protected String getClassName() {
      return name;
    }

    @Override
    protected String getMemberName(String name) {
      return getSymbolName(clazz.getMember(name));
    }
  }

  private void writeClass(ParseTreeWriter writer, ClassSymbol clazz) {
    writer.visitAny(new OptimizedClassTransformer(clazz).transformClass());
  }

  @Override
  protected ParseTree transform(IdentifierExpressionTree tree) {
    return createIdentifier(getSymbolName(project.bindings.getSymbol(tree)));
  }

  @Override
  protected ParseTree transform(MemberExpressionTree tree) {
    return createMemberExpression(transformAny(tree.object), getSymbolName(project.bindings.getSymbol(tree)));
  }
}
