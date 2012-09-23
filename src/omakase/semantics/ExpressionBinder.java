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

import omakase.semantics.symbols.Symbol;
import omakase.semantics.types.Type;
import omakase.syntax.ParseTreeVisitor;
import omakase.syntax.trees.*;

/**
 * Binds Types to expressions.
 * Symbols to identifiers.
 *
 * TODO: Should move to creating full semantic trees.
 */
public class ExpressionBinder extends ParseTreeVisitor {
  private final ExpressionBindingContext context;

  public ExpressionBinder(ExpressionBindingContext context) {
    this.context = context;
  }

  public void bind(ParseTree expression) {
    this.visitAny(expression);
  }

  public void bindBooleanExpression(ParseTree expression) {
    bind(expression, context.getTypes().getBoolType());
  }

  public void bind(ParseTree expression, Type expectedType) {
    bind(expression);
    Type actualType = getExpressionType(expression);
    mustConvert(expression, actualType, expectedType);
  }

  @Override
  protected void visit(ArrayAccessExpressionTree tree) {
    super.visit(tree);

    Type arrayType = getExpressionType(tree.object);
    Type elementType = null;
    boolean anyIndexType = false;
    if (arrayType != null) {
      if (arrayType.isArrayType()) {
        elementType = arrayType.asArrayType().elementType;
      } else if (arrayType.isDynamicType()) {
        elementType = getTypes().getDynamicType();
        anyIndexType = true;
      } else {
        reportError(tree.object, "'%s' not an array type.", arrayType);
      }
    }

    Type indexType = getExpressionType(tree.member);
    if (indexType != null && !anyIndexType) {
      if (!indexType.isNumberType()) {
        reportError(tree.member, "Array index must be number. Found '%s'", indexType);
      }
    }
  }

  @Override
  protected void visit(ArrayLiteralExpressionTree tree) {
    super.visit(tree);
    // TODO:
  }

  @Override
  protected void visit(BinaryExpressionTree tree) {
    super.visit(tree);
    // TODO:
  }

  @Override
  protected void visit(CallExpressionTree tree) {
    super.visit(tree);

    // TODO:
  }

  @Override
  protected void visit(ConditionalExpressionTree tree) {
    // Bind children.
    super.visit(tree);

    ensureBoolType(tree.condition);

    Type left = getExpressionType(tree.left);
    Type right = getExpressionType(tree.right);
    Type result = findCommonType(left, right);
    if (left != null && right != null && result == null) {
      reportError(tree, "Incompatible types in conditional expression. Found '%s' and '%s'.", left, right);
    }
  }

  @Override
  protected void visit(FunctionExpressionTree tree) {
    // TODO
  }

  @Override
  protected void visit(IdentifierExpressionTree tree) {
    String name = tree.name.value;
    Symbol symbol = context.lookupIdentifier(name);
    if (symbol == null) {
      reportError(tree, "'%s' not in scope.", name);
      return;
    }
    if (symbol.isLocalVariable()) {
      // Check for use before declaration.
      if (tree.location.start.isBefore(symbol.location.start())) {
        reportError(tree, "Use of '%s' before declaration.", name);
      }
    }
    setSymbol(tree, symbol);
    setExpressionType(tree, symbol.getType());
  }

  @Override
  protected void visit(LiteralExpressionTree tree) {
    switch (tree.value.kind) {
    case NUMBER_LITERAL:
      setExpressionType(tree, getTypes().getNumberType());
      break;
    case STRING_LITERAL:
      setExpressionType(tree, getTypes().getStringType());
      break;
    case NULL:
      setExpressionType(tree, getTypes().getNullType());
      break;
    case TRUE:
    case FALSE:
      setExpressionType(tree, getTypes().getBoolType());
      break;
    }
  }

  @Override
  protected void visit(MemberExpressionTree tree) {
    super.visit(tree);

    Symbol symbol = getSymbol(tree.object);
    if (symbol != null && symbol.isClass()) {
      // TODO: lookup static member.
    } else {
      Type objectType = getExpressionType(tree.object);
      if (objectType != null) {
        if (objectType.isClassType()) {
          // TODO: lookup instance member.
        } else {
          reportError(tree, "Cannot lookup member on type '%s'.", objectType);
        }
      }
    }
  }

  @Override
  protected void visit(NewExpressionTree tree) {
    super.visit(tree);

    // TODO
  }

  @Override
  protected void visit(ParenExpressionTree tree) {
    super.visit(tree);

    setExpressionType(tree, getExpressionType(tree.expression));
  }

  @Override
  protected void visit(PostfixExpressionTree tree) {
    super.visit(tree);

    // TODO
  }

  @Override
  protected void visit(ThisExpressionTree tree) {
    if (!context.hasThis()) {
      reportError(tree, "No this in scope.");
      return;
    }
    setExpressionType(tree, context.getThisType());
  }

  @Override
  protected void visit(UnaryExpressionTree tree) {
    super.visit(tree);

    // TODO
  }

  private Type findCommonType(Type left, Type right) {
    if (left == null || right == null) {
      return null;
    }
    if (left == right) {
      return left;
    }
    // TODO
    // Classes & null
    // Nullable & Element Type
    // Classes & Base Classes
    // Dynamic

    return null;
  }

  private void ensureBoolType(ParseTree tree) {
    mustConvert(tree, getExpressionType(tree), context.getTypes().getBoolType());
  }

  private void mustConvert(ParseTree tree, Type actualType, Type expectedType) {
    if (canConvert(actualType, expectedType)) {
      return;
    }

    reportError(tree, "Expected expression of type '%s' but found '%s'.", expectedType, actualType);
  }

  private boolean canConvert(Type from, Type to) {
    if (from == null || to == null) {
      // There was an error earlier in the binding.
      return true;
    }

    if (from == to) {
      return true;
    }
    if (to.isDynamicType() || from.isDynamicType()) {
      return true;
    }
    if (from.isNullType() && to.isClassType()) {
      return true;
    }
    if (from.isNullType() && to.isNullableType()) {
      return true;
    }
    if (to.isNullableType() &&
        to.asNullableType().elementType == from) {
      return true;
    }
    return false;
  }

  private Type getExpressionType(ParseTree tree) {
    return context.getResults().getType(tree);
  }

  private void setExpressionType(ParseTree tree, Type type) {
    if (type != null) {
      context.getResults().setType(tree, type);
    }
  }

  private Symbol getSymbol(ParseTree tree) {
    return context.getResults().getSymbol(tree);
  }

  private void setSymbol(ParseTree tree, Symbol symbol) {
    if (symbol != null) {
      context.getResults().setSymbol(tree, symbol);
    }
  }

  private TypeContainer getTypes() {
    return context.getTypes();
  }

  private void reportError(ParseTree tree, String message, Object... args) {
    context.errorReporter().reportError(tree.location.start, message, args);
  }
}
