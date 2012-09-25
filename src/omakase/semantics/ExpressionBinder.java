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
import omakase.semantics.types.KeywordType;
import omakase.semantics.types.Type;
import omakase.semantics.types.TypeContainer;
import omakase.syntax.ParseTreeVisitor;
import omakase.syntax.trees.*;

/**
 * Binds Types to expressions.
 * Symbols to identifiers.
 *
 * TODO: Should move to creating full semantic trees.
 * TODO: Need to check for dynamic types throughout.
 */
public class ExpressionBinder extends ParseTreeVisitor {
  private final ExpressionBindingContext context;

  public ExpressionBinder(ExpressionBindingContext context) {
    this.context = context;
  }

  public Type bind(ParseTree expression) {
    this.visitAny(expression);
    return getExpressionType(expression);
  }

  public Type bindBoolExpression(ParseTree expression) {
    return bind(expression, getBoolType());
  }

  public Type bind(ParseTree expression, Type expectedType) {
    Type actualType = bind(expression);
    if (mustConvert(expression, actualType, expectedType)) {
      return expectedType;
    } else {
      return null;
    }
  }

  @Override
  protected void visit(ArrayAccessExpressionTree tree) {
    Type arrayType = bind(tree.object);
    Type elementType = null;
    if (arrayType != null) {
      if (arrayType.isArrayType()) {
        elementType = arrayType.asArrayType().elementType;
      } else if (arrayType.isDynamicType()) {
        elementType = getTypes().getDynamicType();
      } else {
        reportError(tree.object, "'%s' not an array type.", arrayType);
      }
    }

    if (arrayType != null && arrayType.isDynamicType()) {
      bindAndInfer(tree.member);
    } else {
      bind(tree.member, context.getTypes().getNumberType());
    }

    setExpressionType(tree, elementType);
  }

  @Override
  protected void visit(ArrayLiteralExpressionTree tree) {
    super.visit(tree);
    // TODO:
  }

  @Override
  protected void visit(BinaryExpressionTree tree) {
    switch (tree.operator.kind) {
    case EQUAL:
    case AMPERSAND_EQUAL:
    case BAR_EQUAL:
    case STAR_EQUAL:
    case SLASH_EQUAL:
    case PERCENT_EQUAL:
    case PLUS_EQUAL:
    case MINUS_EQUAL:
    case LEFT_SHIFT_EQUAL:
    case RIGHT_SHIFT_EQUAL:
    case HAT_EQUAL:
      bindAssignmentOperator(tree);
      break;

    case BAR_BAR:
    case AMPERSAND_AMPERSAND:
      bindBinaryExpression(tree, getBoolType());
      break;
    case AMPERSAND:
    case BAR:
      bindBinaryExpression(tree, getNumberType());
      break;
    case EQUAL_EQUAL:
    case NOT_EQUAL:
      bindEqualityOperator(tree);
      break;
    case OPEN_ANGLE:
    case CLOSE_ANGLE:
    case GREATER_EQUAL:
    case LESS_EQUAL:
      // TODO: Comparison of strings.
      bindBinaryExpression(tree, getNumberType());
      break;
    case INSTANCEOF:
      bindAndInfer(tree.left);
      bindType(tree.right);
      // TODO: Check for provably true/false results.
      setExpressionType(tree, getBoolType());
      break;
    case SHIFT_LEFT:
    case SHIFT_RIGHT:
      bindBinaryExpression(tree, getNumberType());
      break;
    case PLUS:
      bindPlusOperator(tree);
      break;
    case MINUS:
    case STAR:
    case SLASH:
    case PERCENT:
      bindBinaryExpression(tree, getNumberType());
      break;
    }
  }

  private void bindAssignmentOperator(BinaryExpressionTree tree) {
    Type leftType;

    switch (tree.operator.kind) {
    case EQUAL:
      leftType = bind(tree.left);
      break;
    case AMPERSAND_EQUAL:
    case BAR_EQUAL:
    case STAR_EQUAL:
    case SLASH_EQUAL:
    case PERCENT_EQUAL:
    case MINUS_EQUAL:
    case LEFT_SHIFT_EQUAL:
    case RIGHT_SHIFT_EQUAL:
    case HAT_EQUAL:
      leftType = bind(tree.left, getNumberType());
      break;
    case PLUS_EQUAL:
      leftType = bind(tree.left);
      if (leftType != null) {
        if (!leftType.isNumberType() && !leftType.isStringType()) {
          reportError(tree.left, "Left hand side of '+=' must be number or string. Found '%s'", leftType);
          leftType = null;
        }
      }
      break;
    default:
      throw new RuntimeException("Unrecognized assignment operator");
    }

    if (leftType != null && !isWritable(tree.left)) {
      reportError(tree.left, "Left hand side of assignment operator must be writable.");
      leftType = null;
    }

    bind(tree.right, leftType);

    setExpressionType(tree, leftType);
    setWritable(tree, true);
  }

  private void bindEqualityOperator(BinaryExpressionTree tree) {
    Type leftType = bindAndInfer(tree.left);
    Type rightType = bindAndInfer(tree.right);
    if (leftType == null || rightType == null) {
      return;
    }
    Type comparisonType = findCommonType(leftType, rightType);
    if (comparisonType == null) {
      reportError(tree, "Cannot compare values of type '%s' and '%s'.", leftType, rightType);
    }

    setExpressionType(tree, getBoolType());
  }

  private void bindPlusOperator(BinaryExpressionTree tree) {
    Type leftType = bind(tree.left);
    Type rightType = bind(tree.right);
    if (leftType == null || rightType == null) {
      return;
    }

    if (leftType.isNumberType() && rightType.isNumberType()) {
      setExpressionType(tree, getNumberType());
    } else if (leftType.isStringType() && rightType.isStringType()) {
      setExpressionType(tree, getStringType());
    } else {
      reportError(tree, "'+' requires both operands to be numbers or strings. Found '%s' and '%s'.", leftType, rightType);
    }
  }

  private void bindBinaryExpression(BinaryExpressionTree tree, Type expectedType) {
    bind(tree.left, expectedType);
    bind(tree.right, expectedType);
    setExpressionType(tree, expectedType);
  }

  @Override
  // TODO: Type inference of function parameters goes here.
  protected void visit(CallExpressionTree tree) {
    Type function = bind(tree.function);
    ArgumentsTree argumentsTree = tree.arguments;
    ImmutableList<? extends ParseTree> arguments = argumentsTree.arguments;
    ArgumentBinder argumentBinder = new ArgumentBinder(argumentsTree).invoke();
    boolean hadError = argumentBinder.hadError();
    ImmutableList<Type> argumentTypes = argumentBinder.getArgumentTypes();

    if (!hadError) {
      if (function.isDynamicType()) {
        setExpressionType(tree, getDynamicType());
        // TODO: Check for unbound function expression.
      } else if (function.isFunctionType()) {
        FunctionType functionType = function.asFunctionType();
        ImmutableList<Type> parameterTypes = functionType.parameterTypes;
        if (parameterTypes.size() != argumentTypes.size()) {
          reportError(tree, "Expected '%d' arguments but found '%d'.", parameterTypes.size(), argumentTypes.size());
          setExpressionType(tree, null);
        } else {
          for (int i = 0; i < parameterTypes.size(); i++) {
            hadError |= mustConvert(arguments.get(i), argumentTypes.get(i), parameterTypes.get(i));
          }
          if (hadError) {
            setExpressionType(tree, null);
          } else {
            setExpressionType(tree, functionType.returnType);
          }
        }
      } else {
        reportError(tree, "Cannot call '%s' because it is not a function.", function);
        setExpressionType(tree, null);
      }
    }
  }

  @Override
  protected void visit(ConditionalExpressionTree tree) {
    bindBoolExpression(tree.condition);
    Type left = bind(tree.left);
    Type right = bind(tree.right);
    Type result = findCommonType(left, right);
    if (left != null && right != null && result == null) {
      reportError(tree, "Incompatible types in conditional expression. Found '%s' and '%s'.", left, right);
    }
  }

  @Override
  protected void visit(FunctionExpressionTree tree) {
    // This'll get bound at the use site.
    setExpressionType(tree, getTypes().getUnboundFunctionLiteralType(tree));
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
        return;
      }
    }
    setSymbol(tree, symbol);
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
    Type objectType = bind(tree.object);
    if (objectType == null) {
      return;
    }
    if (objectType.isDynamicType()) {
      setExpressionType(tree, getDynamicType());
    }
    ClassSymbol clazz;
    boolean isStatic;
    if (objectType.isClassType()) {
      clazz = objectType.asClassType().clazz;
      isStatic = false;
    } else if (objectType.isClassSymbolType()) {
      clazz = getSymbol(tree.object).asClass();
      isStatic = true;
    } else {
      reportError(tree, "Cannot lookup member on expression of type '%s'.", objectType);
      return;
    }

    String name = tree.name.value;
    Symbol result = clazz.lookupMember(name, isStatic);
    if (result == null) {
      reportError(tree, "Class '%s' does not contain a member '%s'.", clazz, name);
      return;
    }
    setSymbol(tree, result);
  }

  @Override
  protected void visit(NewExpressionTree tree) {
    Type constructorType = bind(tree.constructor);
    if (constructorType == null) {
      return;
    }

    ArgumentBinder argumentBinder = new ArgumentBinder(tree.arguments).invoke();
    boolean hadError = argumentBinder.hadError();
    ImmutableList<Type> argumentTypes = argumentBinder.getArgumentTypes();

    if (hadError) {
      return;
    }
    if (constructorType.isDynamicType()) {
      setExpressionType(tree, getDynamicType());
    }
    if (constructorType.isClassSymbolType()) {
      // TODO: Constructor binding here.
      ClassSymbol clazz = getSymbol(tree.constructor).asClass();
      setExpressionType(tree, getTypes().getClassType(clazz));
    } else {
      reportError(tree, "Cannot new an expression of type '%s'.", constructorType);
      return;
    }
  }

  @Override
  protected void visit(ParenExpressionTree tree) {
    setExpressionType(tree, bind(tree.expression));
  }

  @Override
  protected void visit(PostfixExpressionTree tree) {
    if (setExpressionType(tree, bind(tree.operand, getNumberType())) != null) {
      if (!isWritable(tree.operand)) {
        reportError(tree.operand, "Operand of '++' or '--' must be writable.");
        setExpressionType(tree, null);
      } else {
        setWritable(tree, true);
      }
    }
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
    switch (tree.operator.kind) {
    case TYPEOF:
      if (bindAndInfer(tree.operand) != null) {
        // TODO: Better semantics here? Hook into Reflection?
        setExpressionType(tree, getStringType());
      }
      break;
    case PLUS_PLUS:
    case MINUS_MINUS:
      if (bindUnaryOperator(tree, getNumberType()) != null) {
        if (!isWritable(tree.operand)) {
          reportError(tree.operand, "Operand of '++' or '--' must be writable.");
          setExpressionType(tree, null);
        } else {
          setWritable(tree, true);
        }
      }
      break;
    case PLUS:
    case MINUS:
    case TILDE:
      bindUnaryOperator(tree, getNumberType());
      break;
    case BANG:
      bindUnaryOperator(tree, getBoolType());
      break;
    default:
      throw new RuntimeException("Unexpected unary operator.");
    }
  }

  private Type bindUnaryOperator(UnaryExpressionTree tree, Type type) {
    return setExpressionType(tree, bind(tree.operand, type));
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
    // Nullable & null
    // Classes & Base Classes
    // Dynamic
    // Unbound function literals

    return null;
  }

  private void ensureBoolType(ParseTree tree) {
    mustConvert(tree, getExpressionType(tree), getBoolType());
  }

  private boolean mustConvert(ParseTree tree, Type actualType, Type expectedType) {
    // TODO: Check for unbound function expression.
    if (canConvert(actualType, expectedType)) {
      setExpressionType(tree, expectedType);
      return true;
    }

    reportError(tree, "Expected expression of type '%s' but found '%s'.", expectedType, actualType);
    return false;
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
    // TODO: Should classes be nullable by default?
    if (from.isNullType() && to.isClassType()) {
      return true;
    }
    if (from.isNullType() && to.isNullableType()) {
      return true;
    }
    if (to.isNullableType() && to.asNullableType().elementType == from) {
      return true;
    }
    return false;
  }

  private Type bindType(ParseTree type) {
    // TODO: Bind type in class/method/field context.
    return new TypeBinder(context.project).bindType(type);
  }

  private KeywordType getBoolType() {
    return context.getTypes().getBoolType();
  }

  private KeywordType getNumberType() {
    return context.getTypes().getNumberType();
  }

  private KeywordType getStringType() {
    return context.getTypes().getStringType();
  }

  private KeywordType getDynamicType() {
    return context.getTypes().getDynamicType();
  }

  private Type getExpressionType(ParseTree tree) {
    return context.getResults().getType(tree);
  }

  private Type setExpressionType(ParseTree tree, Type type) {
    if (type != null) {
      context.getResults().setType(tree, type);
    }
    return type;
  }

  private Symbol getSymbol(ParseTree tree) {
    return context.getResults().getSymbol(tree);
  }

  private void setSymbol(ParseTree tree, Symbol symbol) {
    if (symbol != null) {
      context.getResults().setSymbol(tree, symbol);
      setExpressionType(tree, symbol.getType());
      setWritable(tree, symbol.isWritable());
    }
  }

  private void setWritable(ParseTree tree, boolean value) {
    context.getResults().setWritable(tree, value);
  }

  private boolean isWritable(ParseTree tree) {
    return context.getResults().isWritable(tree);
  }

  private TypeContainer getTypes() {
    return context.getTypes();
  }

  private void reportError(ParseTree tree, String message, Object... args) {
    context.errorReporter().reportError(tree.location.start, message, args);
  }

  public Type bindAndInfer(ParseTree expression) {
    Type type = bind(expression);
    if (type == null) {
      return null;
    }
    if (type.isUnboundFunctionLiteral()) {
      // TODO: Bind the function expression and infer the return type if possible.
    }
    return null;
  }

  private class ArgumentBinder {
    private ArgumentsTree tree;
    private boolean hadError;
    private ImmutableList<Type> argumentTypes;

    public ArgumentBinder(ArgumentsTree tree) {
      this.tree = tree;
    }

    public boolean hadError() {
      return hadError;
    }

    public ImmutableList<Type> getArgumentTypes() {
      return argumentTypes;
    }

    public ArgumentBinder invoke() {
      hadError = false;
      argumentTypes = getTypes().getEmptyTypeArray();
      for (ParseTree argument : tree.arguments) {
        Type argumentType = bind(argument);
        if (argumentType == null) {
          hadError = true;
        } else {
          getTypes().getTypeArray(argumentTypes, argumentType);
        }
      }
      return this;
    }
  }
}
