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

package omakase.visitorgenerator;

import com.google.common.io.Files;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.ParseTreeKind;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class Program {

  static class TreeInfo {
    public String className;
    public ParseTreeKind kind;
    public String asName;
    public Class clazz;
  }

  static final String packagePrefix = "omakase.syntax.trees.";

  public static void main(String[] args) {
    try {
      printVisitor(args[0], loadTrees(), new PrintStream(args[1]));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static void printVisitor(String arg, ArrayList<TreeInfo> trees, PrintStream out) {
    printHeader(out, arg);
    printVisitAny(out, trees);
    out.println("    }");
    out.println("  }");
    printTreeVisits(out, trees);
    out.println("}");
  }

  private static void printHeader(PrintStream out, String headerFileName) {
    try {
      out.write(Files.toByteArray(new File(headerFileName)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printTreeVisits(PrintStream out, ArrayList<TreeInfo> trees) {
    for (TreeInfo tree: trees) {
      out.println();
      out.printf("  protected void visit(%s tree) {\n", tree.className);
      for (Field field: tree.clazz.getFields()) {
        Class fieldType = field.getType();
        if (ParseTree.class.isAssignableFrom(fieldType)) {
          out.printf("    visitAny(tree.%s);\n", field.getName());
        } else {
          if (List.class.isAssignableFrom((fieldType))) {
            out.printf("    visitList(tree.%s);\n", field.getName());
          }
        }
      }
      out.println("  }");
    }
  }

  private static void printVisitAny(PrintStream out, ArrayList<TreeInfo> trees) {
    for (TreeInfo tree: trees) {
      out.printf("    case %s:\n", tree.kind.name());
      out.printf("      visit(tree.%s());\n", tree.asName);
      out.println("      break;");
    }
  }

  private static ArrayList<TreeInfo> loadTrees() {
    ArrayList<TreeInfo> result = new ArrayList<TreeInfo>();
    ParseTreeKind[] kinds = ParseTreeKind.values();
    for (ParseTreeKind kind: kinds) {
      TreeInfo info = new TreeInfo();
      info.kind = kind;
      String camelName = upperCaseToCamelCase(kind.name());
      info.asName = "as" + camelName;
      String javascriptPrefix = "Javascript";
      String fullClassName;
      if (camelName.startsWith(javascriptPrefix)) {
        info.className = packagePrefix + "javascript." + camelName.substring(javascriptPrefix.length()) + "Tree";
        fullClassName = info.className;
      } else {
        info.className = camelName + "Tree";
        fullClassName = packagePrefix + info.className;
      }

      try {
        info.clazz = Class.forName(fullClassName);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      // TODO: Validate

      result.add(info);
    }
    Collections.sort(result, new Comparator<TreeInfo>() {
      public int compare(TreeInfo a, TreeInfo b) {
        return a.className.compareTo(b.className);
      }
    });

    return result;
  }

  private static String upperCaseToCamelCase(String name) {
    StringBuilder result = new StringBuilder();
    boolean isFirst = true;
    for (int index = 0; index < name.length(); index++) {
      if (name.charAt(index) == '_') {
        isFirst = true;
      } else if (isFirst) {
        result.append(name.charAt(index));
        isFirst = false;
      } else {
        result.append(Character.toLowerCase(name.charAt(index)));
      }
    }
    return result.toString();
  }
}
