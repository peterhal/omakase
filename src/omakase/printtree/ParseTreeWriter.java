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
import omakase.syntax.ParseTreeVisitor;
import omakase.syntax.tokens.*;
import omakase.syntax.trees.*;

import java.io.PrintStream;

/**
 *
 */
public class ParseTreeWriter extends ParseTreeVisitor {

  private int indent;
  private PrintStream out;
  private boolean startOfLine;
  private final int INDENT = 2;

  public ParseTreeWriter(PrintStream out) {
    this.indent = 0;
    this.out = out;
    this.startOfLine = true;
  }

  @Override
  protected void visit(ArgumentsTree tree) {
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.arguments);
    write(TokenKind.CLOSE_PAREN);
  }

  @Override
  protected void visit(FunctionExpressionTree tree) {
    visit(tree.parameters);
    // TODO: write(=>)
    visit(tree.body);
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
  protected void visit(MethodDeclarationTree tree) {
    if (tree.isNative) {
      write(TokenKind.NATIVE);
    }
    write(tree.name);
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.formals);
    write(TokenKind.CLOSE_PAREN);

    visitAny(tree.body);
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
  }

  protected void visit(omakase.syntax.trees.javascript.CallExpressionTree tree) {
    visitAny(tree.function);
    visit(tree.arguments);
  }

  protected void visit(omakase.syntax.trees.javascript.CaseClauseTree tree) {
    write(TokenKind.JS_CASE);
    visitAny(tree.expression);
    write(TokenKind.JS_COLON);
    visitList(tree.statements);
  }

  protected void visit(omakase.syntax.trees.javascript.CatchClauseTree tree) {
    write(TokenKind.JS_CATCH);
    write(TokenKind.JS_OPEN_PAREN);
    write(tree.identifier);
    write(TokenKind.JS_CLOSE_PAREN);
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
  }

  protected void visit(omakase.syntax.trees.javascript.DebuggerStatementTree tree) {
    write(TokenKind.JS_DEBUGGER);
    write(TokenKind.JS_SEMI_COLON);
  }

  protected void visit(omakase.syntax.trees.javascript.DefaultClauseTree tree) {
    write(TokenKind.JS_DEFAULT);
    write(TokenKind.JS_COLON);
    visitList(tree.statements);
  }

  protected void visit(omakase.syntax.trees.javascript.DoStatementTree tree) {
    write(TokenKind.JS_DO);
    visitAny(tree.statement);
    write(TokenKind.JS_WHILE);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.JS_CLOSE_PAREN);
    write(TokenKind.JS_SEMI_COLON);
  }

  protected void visit(omakase.syntax.trees.javascript.EmptyStatementTree tree) {
    write(TokenKind.JS_SEMI_COLON);
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
    write("get"); // TODO
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
    visitAny(tree.ifClause);
    if (tree.elseClause != null) {
      visitAny(tree.elseClause);
    }
  }

  protected void visit(omakase.syntax.trees.javascript.LabelledStatementTree tree) {
    write(tree.label);
    write(TokenKind.JS_COLON);
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
    writeCommaSeparatedList(tree.initializers);
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
  }

  protected void visit(omakase.syntax.trees.javascript.SetAccessorTree tree) {
    write("set"); // TODO
    write(tree.propertyName);
    write(TokenKind.JS_OPEN_PAREN);
    write(tree.parameterName);
    write(TokenKind.JS_CLOSE_PAREN);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.SwitchStatementTree tree) {
    write(TokenKind.JS_SWITCH);
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
  }

  protected void visit(omakase.syntax.trees.javascript.TryStatementTree tree) {
    write(TokenKind.JS_TRY);
    visitAny(tree.body);
    visitAny(tree.catchClause);
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
  }

  protected void visit(omakase.syntax.trees.javascript.WhileStatementTree tree) {
    write(TokenKind.JS_WHILE);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.condition);
    write(TokenKind.JS_CLOSE_PAREN);
    visitAny(tree.body);
  }

  protected void visit(omakase.syntax.trees.javascript.WithStatementTree tree) {
    write(TokenKind.JS_WITH);
    write(TokenKind.JS_OPEN_PAREN);
    visitAny(tree.expression);
    write(TokenKind.JS_CLOSE_PAREN);
    visitAny(tree.body);
  }

  private void writeCommaSeparatedList(ImmutableList<ParseTree> trees) {
    writeList(trees, TokenKind.COMMA.toString());
  }

  private void writeCommaSeparatedTokenList(ImmutableList<? extends Token> trees) {
    writeTokenList(trees, TokenKind.COMMA.toString());
  }

  private void writeList(ImmutableList<ParseTree> trees, String separator) {
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
    case NUMBER:
      write(token.asNumericLiteral());
      break;
    case STRING:
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
    out.println();
    this.startOfLine = true;
  }

  private void write(Token token) {
    write(token.toValueString());
  }

  private void write(TokenKind kind) {
    write(kind.toString());
  }

  private void write(String value) {
    if (this.startOfLine) {
      for (int i = 0; i < indent; i++) {
        out.print(' ');
      }
      this.startOfLine = false;
    } else {
      out.print(' ');
    }
    out.print(value);
  }

  private void outdent() {
    indent  -= INDENT;
  }

  private void indent() {
    indent  += INDENT;
  }
}
