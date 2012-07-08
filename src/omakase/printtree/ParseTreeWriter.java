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
import omakase.syntax.trees.BlockTree;
import omakase.syntax.trees.CallExpressionTree;
import omakase.syntax.trees.ExpressionStatementTree;
import omakase.syntax.trees.SimpleNameExpressionTree;
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
  protected void visit(FunctionExpressionTree tree) {
    visit(tree.parameters);
    visit(tree.body);
  }

  @Override
  protected void visit(CallExpressionTree tree) {
    visitAny(tree.function);
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.arguments);
    write(TokenKind.CLOSE_PAREN);
  }

  @Override
  protected void visit(SimpleNameExpressionTree tree) {
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
    write(tree.name);
    write(TokenKind.OPEN_PAREN);
    writeCommaSeparatedList(tree.formals);
    write(TokenKind.CLOSE_PAREN);

    visit(tree.body);
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

  private void writeCommaSeparatedList(ImmutableList<ParseTree> trees) {
    writeList(trees, TokenKind.COMMA.toString());
  }

  private void writeList(ImmutableList<ParseTree> trees, String separator) {
    boolean first = true;
    for (ParseTree tree: trees) {
      if (!first) {
        write(separator);
      }
      visitAny(tree);
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
