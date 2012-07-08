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

package omakase.codegeneration;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import omakase.printtree.ParseTreeWriter;
import omakase.syntax.Parser;
import omakase.syntax.Scanner;
import omakase.syntax.tokens.Token;
import omakase.syntax.trees.ParseTree;
import omakase.util.ConsoleErrorReporter;
import omakase.util.SourceFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 */
public class Program {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Missing filename argument.");
      return;
    }

    final String filename = args[0];
    String source;
    try {
      source = Files.toString(new File(filename), Charset.defaultCharset());
    } catch (IOException e) {
      System.err.format("Error '%s' attempting to open file '%s'.\n", e.toString(), filename);
      source = null;
    }
    if (source == null) {
      return;
    }

    SourceFile file = new SourceFile(filename, source);
    ImmutableList<Token> tokens = Scanner.scanFile(ConsoleErrorReporter.reporter, file);
    if (!ConsoleErrorReporter.reporter.hadError()) {
      ParseTree tree = Parser.parse(ConsoleErrorReporter.reporter, tokens);
      if (!ConsoleErrorReporter.reporter.hadError()) {
        JavascriptTransformer transformer = new JavascriptTransformer();
        ParseTree result = transformer.transformAny(tree);
        ParseTreeWriter writer = new ParseTreeWriter(System.out);
        writer.visitAny(result);
      }
    }

  }
}