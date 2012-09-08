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

package omakase.printtree;

import com.google.common.collect.ImmutableList;
import omakase.syntax.JavascriptPredefinedNames;
import omakase.syntax.ParseTreeVisitor;
import omakase.syntax.tokens.*;
import omakase.syntax.trees.*;
import omakase.util.IndentedWriter;

import java.io.PrintStream;

/**
 *
 */
public class ParseTreeWriter extends ParseTreeVisitor {

  private final IndentedWriter indentedWriter;

  public ParseTreeWriter(PrintStream out) {
    this.indentedWriter = new IndentedWriter(out);
  }

  @Override
  protected void visit(ArgumentsTree tree) {
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.arguments);
    write(TokenKind.CLOSE_PAREN);
  }

  @Override
  protected void visit(ArrayAccessExpressionTree tree) {
    visitAny(tree.object);
    write(TokenKind.OPEN_SQUARE);
    visitAny(tree.member);
    write(TokenKind.CLOSE_SQUARE);
  }

  @Override
  protected void visit(ArrayLiteralExpressionTree tree) {
    write(TokenKind.OPEN_SQUARE);
    writeCommaSeparatedList(tree.elements);
    write(TokenKind.CLOSE_SQUARE);
  }

  @Override
  protected void visit(ArrayTypeTree tree) {
    visitAny(tree.elementType);
    write(TokenKind.OPEN_SQUARE);
    write(TokenKind.CLOSE_SQUARE);
  }

  @Override
  protected void visit(BinaryExpressionTree tree) {
    visitAny(tree.left);
    write(tree.operator);
    visitAny(tree.right);
  }

  @Override
  protected void visit(BreakStatementTree tree) {
    write(TokenKind.BREAK);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(CaseClauseTree tree) {
    write(TokenKind.CASE);
    visitAny(tree.expression);
    write(TokenKind.COLON);
    writeLine();
    indent();
    visitList(tree.statements);
    outdent();
  }

  @Override
  protected void visit(CatchClauseTree tree) {
    write(TokenKind.CATCH);
    write(TokenKind.OPEN_PAREN);
    write(tree.identifier);
    write(TokenKind.CLOSE_PAREN);
    writeLine();
    visitAny(tree.block);
  }

  @Override
  protected void visit(ConditionalExpressionTree tree) {
    visitAny(tree.condition);
    write(TokenKind.QUESTION);
    visitAny(tree.left);
    write(TokenKind.COLON);
    visitAny(tree.right);
  }

  @Override
  protected void visit(ContinueStatementTree tree) {
    write(TokenKind.CONTINUE);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(DebuggerStatementTree tree) {
    write(TokenKind.DEBUGGER);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(DefaultClauseTree tree) {
    write(TokenKind.DEFAULT);
    write(TokenKind.COLON);
    writeLine();
    indent();
    visitList(tree.statements);
    outdent();
  }

  @Override
  protected void visit(DoStatementTree tree) {
    write(TokenKind.DO);
    visitAny(tree.statement);
    write(TokenKind.WHILE);
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.CLOSE_PAREN);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(EmptyStatementTree tree) {
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(ForInStatementTree tree) {
    write(TokenKind.FOR);
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.element);
    write(TokenKind.IN);
    visitAny(tree.collection);
    write(TokenKind.CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  @Override
  protected void visit(ForStatementTree tree) {
    write(TokenKind.FOR);
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.initializer);
    if (tree.initializer == null || !tree.initializer.isVariableStatement()) {
      write(TokenKind.SEMI_COLON);
    }
    visitAny(tree.condition);
    write(TokenKind.SEMI_COLON);
    visitAny(tree.increment);
    write(TokenKind.CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  @Override
  protected void visit(FormalParameterListTree tree) {
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.parameters);
    write(TokenKind.CLOSE_PAREN);
  }

  @Override
  protected void visit(FunctionExpressionTree tree) {
    visit(tree.parameters);
    write(TokenKind.ARROW);
    visitAny(tree.body);
  }

  @Override
  protected void visit(FunctionTypeTree tree) {
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.argumentTypes);
    write(TokenKind.CLOSE_PAREN);
    write(TokenKind.ARROW);
    visitAny(tree.returnType);
  }

  @Override
  protected void visit(IfStatementTree tree) {
    write(TokenKind.IF);
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.CLOSE_PAREN);
    indent();
    writeLine();
    outdent();
    visitAny(tree.ifClause);
    if (tree.elseClause != null) {
      write(TokenKind.ELSE);
      writeLine();
      indent();
      visitAny(tree.elseClause);
      outdent();
    }
  }

  @Override
  protected void visit(KeywordTypeTree tree) {
    write(tree.type);
  }

  @Override
  protected void visit(NamedTypeTree tree) {
    if (tree.element != null) {
      visitAny(tree.element);
      write(TokenKind.PERIOD);
    }
    write(tree.name);
    visit(tree.typeArguments);
  }

  @Override
  protected void visit(NullableTypeTree tree) {
    visitAny(tree.elementType);
    write(TokenKind.QUESTION);
  }

  @Override
  protected void visit(ReturnStatementTree tree) {
    write(TokenKind.RETURN);
    visitAny(tree.value);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(SourceFileTree tree) {
    visitList(tree.declarations);
    writeLine();
  }

  @Override
  protected void visit(SwitchStatementTree tree) {
    write(TokenKind.SWITCH);
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.expression);
    write(TokenKind.CLOSE_PAREN);
    writeLine();
    write(TokenKind.OPEN_CURLY);
    visitList(tree.caseClauses);
    write(TokenKind.CLOSE_CURLY);
  }

  @Override
  protected void visit(ThisExpressionTree tree) {
    write(TokenKind.THIS);
  }

  @Override
  protected void visit(ThrowStatementTree tree) {
    write(TokenKind.THROW);
    visitAny(tree.expression);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(TryStatementTree tree) {
    write(TokenKind.TRY);
    writeLine();
    visitAny(tree.body);
    visitAny(tree.catchClause);
    if (tree.finallyClause != null) {
      write(TokenKind.FINALLY);
      visitAny(tree.finallyClause);
    }
  }

  @Override
  protected void visit(TypeArgumentListTree tree) {
    write(TokenKind.OPEN_ANGLE);
    writeCommaSeparatedList(tree.typeArguments);
    write(TokenKind.CLOSE_ANGLE);
  }

  @Override
  protected void visit(UnaryExpressionTree tree) {
    write(tree.operator);
    visitAny(tree.operand);
  }

  @Override
  protected void visit(VariableDeclarationTree tree) {
    write(tree.name);
    if (tree.initializer != null) {
      write(TokenKind.EQUAL);
      visitAny(tree.initializer);
    }
  }

  @Override
  protected void visit(VariableStatementTree tree) {
    write(TokenKind.VAR);
    writeCommaSeparatedList(tree.declarations);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(WhileStatementTree tree) {
    write(TokenKind.WHILE);
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  @Override
  protected void visit(CallExpressionTree tree) {
    visitAny(tree.function);
    visitAny(tree.arguments);
  }

  @Override
  protected void visit(IdentifierExpressionTree tree) {
    write(tree.name);
  }

  @Override
  protected void visit(LiteralExpressionTree tree) {
    writeAny(tree.value);
  }

  @Override
  protected void visit(ExpressionStatementTree tree) {
    visitAny(tree.expression);
    write(TokenKind.SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(BlockTree tree) {
    write(TokenKind.OPEN_CURLY);
    writeLine();
    indent();

    visitList(tree.statements);

    outdent();
    write(TokenKind.CLOSE_CURLY);
    writeLine();
  }

  @Override
  protected void visit(ParameterDeclarationTree tree) {
    write(tree.name);
  }

  @Override
  protected void visit(FieldDeclarationTree tree) {
    if (tree.isStatic) {
      write(TokenKind.STATIC);
    }
    visitAny(tree.type);
    writeCommaSeparatedList(tree.declarations);
    write(TokenKind.SEMI_COLON);
  }

  @Override
  protected void visit(MethodDeclarationTree tree) {
    if (tree.isStatic) {
      write(TokenKind.STATIC);
    }
    if (tree.isNative) {
      write(TokenKind.NATIVE);
    }
    visitAny(tree.returnType);
    write(tree.name);
    visitAny(tree.formals);

    visitAny(tree.body);
  }

  @Override
  protected void visit(MemberExpressionTree tree) {
    visitAny(tree.object);
    write(TokenKind.PERIOD);
    write(tree.name);
  }

  @Override
  protected void visit(NewExpressionTree tree) {
    write(TokenKind.NEW);
    visitAny(tree.constructor);
    visit(tree.arguments);
  }

  @Override
  protected void visit(ParenExpressionTree tree) {
    write(TokenKind.OPEN_PAREN);
    visitAny(tree.expression);
    write(TokenKind.CLOSE_PAREN);
  }

  @Override
  protected void visit(PostfixExpressionTree tree) {
    visitAny(tree.operand);
    write(tree.operator);
  }

  @Override
  protected void visit(ClassDeclarationTree tree) {
    write(TokenKind.CLASS);
    write(tree.name);
    write(TokenKind.OPEN_CURLY);
    writeLine();

    indent();
    visitList(tree.members);
    outdent();

    write(TokenKind.CLOSE_CURLY);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.ArgumentsTree tree) {
    write(TokenKind.JS_OPEN_PAREN);
    writeCommaSeparatedList(tree.arguments);
    write(TokenKind.JS_CLOSE_PAREN);
  }

  protected void visit(omakase.syntax.trees.javascript.ArrayAccessExpressionTree tree) {
    visitAny(tree.object);
    write(TokenKind.JS_OPEN_SQUARE);
    visitAny(tree.member);
    write(TokenKind.JS_CLOSE_SQUARE);
  }

  protected void visit(omakase.syntax.trees.javascript.ArrayLiteralExpressionTree tree) {
    write(TokenKind.JS_OPEN_SQUARE);
    writeCommaSeparatedList(tree.elements);
    write(TokenKind.JS_CLOSE_SQUARE);
  }

  protected void visit(omakase.syntax.trees.javascript.BinaryExpressionTree tree) {
    visitAny(tree.left);
    write(tree.operator.kind);
    visitAny(tree.right);
  }

  protected void visit(omakase.syntax.trees.javascript.BlockTree tree) {
    write(TokenKind.JS_OPEN_CURLY);
    writeLine();
    indent();

    visitList(tree.statements);

    outdent();
    write(TokenKind.JS_CLOSE_CURLY);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.BreakStatementTree tree) {
    write(TokenKind.JS_BREAK);
    if (tree.label != null) {
      write(tree.label);
    }
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.CallExpressionTree tree) {
    visitAny(tree.function);
    visit(tree.arguments);
  }

  protected void visit(omakase.syntax.trees.javascript.CaseClauseTree tree) {
    write(TokenKind.JS_CASE);
    visitAny(tree.expression);
    write(TokenKind.JS_COLON);
    writeLine();
    indent();
    visitList(tree.statements);
    outdent();
  }

  protected void visit(omakase.syntax.trees.javascript.CatchClauseTree tree) {
    write(TokenKind.JS_CATCH);
    write(TokenKind.JS_OPEN_PAREN);
    write(tree.identifier);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    visitAny(tree.block);
  }

  protected void visit(omakase.syntax.trees.javascript.CommaExpressionTree tree) {
    writeCommaSeparatedList(tree.expressions);
  }

  protected void visit(omakase.syntax.trees.javascript.ConditionalExpressionTree tree) {
    visitAny(tree.condition);
    write(TokenKind.JS_QUESTION);
    visitAny(tree.left);
    write(TokenKind.JS_COLON);
    visitAny(tree.right);
  }

  protected void visit(omakase.syntax.trees.javascript.ContinueStatementTree tree) {
    write(TokenKind.JS_CONTINUE);
    if (tree.label != null) {
      write(tree.label);
    }
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.DebuggerStatementTree tree) {
    write(TokenKind.JS_DEBUGGER);
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.DefaultClauseTree tree) {
    write(TokenKind.JS_DEFAULT);
    write(TokenKind.JS_COLON);
    writeLine();
    indent();
    visitList(tree.statements);
    outdent();
  }

  protected void visit(omakase.syntax.trees.javascript.DoStatementTree tree) {
    write(TokenKind.JS_DO);
    writeLine();
    visitAny(tree.statement);
    write(TokenKind.JS_WHILE);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.JS_CLOSE_PAREN);
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  @Override
  protected void visit(omakase.syntax.trees.javascript.ElisionTree tree) {
    write(TokenKind.JS_COMMA);
  }

  protected void visit(omakase.syntax.trees.javascript.EmptyStatementTree tree) {
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.ExpressionStatementTree tree) {
    visitAny(tree.expression);
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.ForInStatementTree tree) {
    write(TokenKind.JS_FOR);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.element);
    write(TokenKind.JS_IN);
    visitAny(tree.collection);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.ForStatementTree tree) {
    write(TokenKind.JS_FOR);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.initializer);
    write(TokenKind.JS_SEMI_COLON);
    visitAny(tree.condition);
    write(TokenKind.JS_SEMI_COLON);
    visitAny(tree.increment);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.FormalParameterListTree tree) {
    writeCommaSeparatedTokenList(tree.parameters);
  }

  protected void visit(omakase.syntax.trees.javascript.FunctionExpressionTree tree) {
    write(TokenKind.JS_FUNCTION);
    if (tree.name != null) {
      write(tree.name);
    }
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.parameters);
    write(TokenKind.JS_CLOSE_PAREN);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.GetAccessorTree tree) {
    write(JavascriptPredefinedNames.GET);
    write(tree.propertyName);
    write(TokenKind.JS_OPEN_PAREN);
    write(TokenKind.JS_CLOSE_PAREN);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.IdentifierExpressionTree tree) {
    write(tree.name);
  }

  protected void visit(omakase.syntax.trees.javascript.IfStatementTree tree) {
    write(TokenKind.JS_IF);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    indent();
    visitAny(tree.ifClause);
    outdent();
    if (tree.elseClause != null) {
      write(TokenKind.JS_ELSE);
      writeLine();
      indent();
      visitAny(tree.elseClause);
      outdent();
    }
  }

  protected void visit(omakase.syntax.trees.javascript.LabelledStatementTree tree) {
    write(tree.label);
    write(TokenKind.JS_COLON);
    writeLine();
    visitAny(tree.statement);
  }

  protected void visit(omakase.syntax.trees.javascript.LiteralExpressionTree tree) {
    write(tree.literal);
  }

  protected void visit(omakase.syntax.trees.javascript.MemberExpressionTree tree) {
    visitAny(tree.object);
    write(TokenKind.JS_PERIOD);
    write(tree.name);
  }

  protected void visit(omakase.syntax.trees.javascript.NewExpressionTree tree) {
    write(TokenKind.JS_NEW);
    visitAny(tree.constructor);
    visitAny(tree.arguments);
  }

  protected void visit(omakase.syntax.trees.javascript.ObjectLiteralExpressionTree tree) {
    write(TokenKind.JS_OPEN_CURLY);
    indent();
    writeCommaSeparatedList(tree.initializers);
    outdent();
    write(TokenKind.JS_CLOSE_CURLY);
  }

  protected void visit(omakase.syntax.trees.javascript.ParenExpressionTree tree) {
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.expression);
    write(TokenKind.JS_CLOSE_PAREN);
  }

  protected void visit(omakase.syntax.trees.javascript.PostfixExpressionTree tree) {
    visitAny(tree.operand);
    write(tree.operator);
  }

  protected void visit(omakase.syntax.trees.javascript.ProgramTree tree) {
    visitList(tree.sourceElements);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.PropertyAssignmentTree tree) {
    write(tree.propertyName);
    write(TokenKind.JS_COLON);
    visitAny(tree.value);
  }

  protected void visit(omakase.syntax.trees.javascript.ReturnStatementTree tree) {
    write(TokenKind.JS_RETURN);
    visitAny(tree.value);
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.SetAccessorTree tree) {
    write(JavascriptPredefinedNames.SET);
    write(tree.propertyName);
    write(TokenKind.JS_OPEN_PAREN);
    write(tree.parameterName);
    write(TokenKind.JS_CLOSE_PAREN);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.SwitchStatementTree tree) {
    write(TokenKind.JS_SWITCH);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.expression);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    write(TokenKind.JS_OPEN_CURLY);
    visitList(tree.caseClauses);
    write(TokenKind.JS_CLOSE_CURLY);
  }

  protected void visit(omakase.syntax.trees.javascript.ThisExpressionTree tree) {
    write(TokenKind.JS_THIS);
  }

  protected void visit(omakase.syntax.trees.javascript.ThrowStatementTree tree) {
    write(TokenKind.JS_THROW);
    visitAny(tree.expression);
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.TryStatementTree tree) {
    write(TokenKind.JS_TRY);
    writeLine();
    visitAny(tree.body);
    if (tree.catchClause != null) {
      visitAny(tree.catchClause);
    }
    if (tree.finallyClause != null) {
      write(TokenKind.JS_FINALLY);
      visitAny(tree.finallyClause);
    }
  }

  protected void visit(omakase.syntax.trees.javascript.UnaryExpressionTree tree) {
    write(tree.operator);
    visitAny(tree.operand);
  }

  protected void visit(omakase.syntax.trees.javascript.VariableDeclarationTree tree) {
    write(tree.name);
    if (tree.initializer != null) {
      write(TokenKind.JS_EQUAL);
      visitAny(tree.initializer);
    }
  }

  protected void visit(omakase.syntax.trees.javascript.VariableStatementTree tree) {
    write(TokenKind.JS_VAR);
    writeCommaSeparatedList(tree.declarations);
    write(TokenKind.JS_SEMI_COLON);
    writeLine();
  }

  protected void visit(omakase.syntax.trees.javascript.WhileStatementTree tree) {
    write(TokenKind.JS_WHILE);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.WithStatementTree tree) {
    write(TokenKind.JS_WITH);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.expression);
    write(TokenKind.JS_CLOSE_PAREN);
    writeLine();
    visitAny(tree.body);
  }

  private void writeCommaSeparatedList(ImmutableList<? extends ParseTree> trees) {
    writeList(trees, TokenKind.COMMA.toString());
  }

  private void writeCommaSeparatedTokenList(ImmutableList<? extends Token> trees) {
    writeTokenList(trees, TokenKind.COMMA.toString());
  }

  private void writeList(ImmutableList<? extends ParseTree> trees, String separator) {
    boolean first = true;
    for (ParseTree tree: trees) {
      if (!first) {
        write(separator);
        first = false;
      }
      visitAny(tree);
    }
  }

  private void writeTokenList(ImmutableList<? extends Token> tokens, String separator) {
    boolean first = true;
    for (Token token: tokens) {
      if (!first) {
        write(separator);
        first = false;
      }
      write(token);
    }
  }

  private void write(IdentifierToken name) {
    write(name.value);
  }

  private void writeAny(Token token) {
    switch (token.kind) {
    case IDENTIFIER:
      write(token.asIdentifier());
      break;
    case NUMBER_LITERAL:
      write(token.asNumericLiteral());
      break;
    case STRING_LITERAL:
      write(token.asStringLiteral());
      break;
    default:
      write(token.kind);
      break;
    }
  }

  private void write(StringLiteralToken token) {
    // TODO: escape string
    write("\"" + token.value + "\"");
  }

  private void write(NumericLiteralToken token) {
    write(String.format(" %d", token.value));
  }

  private void writeLine() {
    indentedWriter.writeLine();
  }

  private void write(Token token) {
    write(token.toValueString());
  }

  private void write(TokenKind kind) {
    write(kind.value());
  }

  private void write(String value) {
    indentedWriter.write(value);
  }

  private void outdent() {
    indentedWriter.outdent();
  }

  private void indent() {
    indentedWriter.indent();
  }
}
