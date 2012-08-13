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

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import omakase.syntax.trees.ParseTree;
import omakase.syntax.trees.ParseTreeKind;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Generates ParseTreeVisitor and ParseTreeTransformer from the ParseTree class declarations.
 *
 * Command Line Arguments:
 *  src\omakase\visitorgenerator\ParseTreeVisitor.header
 *  src\omakase\syntax\ParseTreeVisitor.java
 *  src\omakase\visitorgenerator\ParseTreeTransformer.header
 *  src\omakase\syntax\ParseTreeTransformer.java
 *  src\omakase\visitorgenerator\ParseTree.header
 *  src\omakase\syntax\trees\ParseTree.java
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
    final String visitorHeaderFileName = args[0];
    final String visitorOutputFilename = args[1];
    final String transformerHeaderFilename = args[2];
    final String transformerOutputFilename = args[3];
    final String parseTreeHeaderFilename = args[4];
    final String parseTreeOutputFilename = args[5];

    try {
      final ArrayList<TreeInfo> trees = loadTrees();
      printVisitor(visitorHeaderFileName, trees, new PrintStream(visitorOutputFilename));
      printTransformer(transformerHeaderFilename, trees, new PrintStream(transformerOutputFilename));
      printParseTree(parseTreeHeaderFilename, trees, new PrintStream(parseTreeOutputFilename));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static void printParseTree(String headerFilename, ArrayList<TreeInfo> trees, PrintStream out) {
    printHeader(out, headerFilename);
    printAsMethods(out, trees);
    out.println("}"); // class
  }

  private static void printAsMethods(PrintStream out, ArrayList<TreeInfo> trees) {
    for (TreeInfo tree: trees) {
      out.println();
      out.printf("  public %s %s() {\n", tree.className, tree.asName);
      out.printf("    return (%s) this;\n", tree.className);
      out.println("  }");
    }
  }

  private static void printTransformer(String headerFilename, ArrayList<TreeInfo> trees, PrintStream out) {
    printHeader(out, headerFilename);
    printTransformAny(out, trees);
    printTreeTransforms(out, trees);
    out.println("}"); // class
  }

  private static void printTreeTransforms(PrintStream out, ArrayList<TreeInfo> trees) {
    Map<Class, TreeInfo> classMap = new HashMap<Class, TreeInfo>();

    for (TreeInfo tree: trees) {
      classMap.put(tree.clazz, tree);
    }
    for (TreeInfo tree: trees) {
      out.println();
      out.printf("  protected ParseTree transform(%s tree) {\n", tree.className);
      // transform each field
      for (Field field: tree.clazz.getFields()) {
        if (isParseTreeType(field)) {
          out.printf("    ParseTree %s = transformAny(tree.%s);\n", field.getName(), field.getName());
        } else if (isParseTreeListType(field)) {
          out.printf("    ImmutableList<ParseTree> %s = transformList(tree.%s);\n", field.getName(), field.getName());
        }
      }

      // test for no change and early return
      boolean firstField = true;
      for (Field field: tree.clazz.getFields()) {
        if (isParseTreeOrListType(field)) {
          if (firstField) {
            firstField = false;
            out.print("    if (");
          } else {
            out.println(" &&");
            out.print("        ");
          }
          out.printf("%s == tree.%s", field.getName(), field.getName());
        }
      }
      if (firstField) {
        out.println("    return tree;");
      } else {
        out.println(") {");
        out.println("      return tree;");
        out.println("    }");

        // return new tree
        out.printf("    return new %s(\n", tree.className);
        out.print("        null");
        for (Field field: tree.clazz.getFields()) {
          if (field.getDeclaringClass() == tree.clazz) {
            out.println(",");
            out.printf("        ");
            if (isParseTreeOrListType(field)){
              out.printf("%s", field.getName());
              if (isParseTreeType(field) && field.getType() != ParseTree.class) {
                out.printf(".%s()", classMap.get(field.getType()).asName);
              }
            } else {
              out.printf("tree.%s", field.getName());
            }
          }
        }
        out.println(");");
      }

      out.println("  }");  // transform
    }
  }

  private static boolean isParseTreeOrListType(Field field) {
    return isParseTreeType(field) || isParseTreeListType(field);
  }

  private static void printTransformAny(PrintStream out, ArrayList<TreeInfo> trees) {
    for (TreeInfo tree: trees) {
      out.printf("    case %s:\n", tree.kind.name());
      out.printf("      return transform(tree.%s());\n", tree.asName);
    }
    out.println("    default:");
    out.println("      throw new RuntimeException(\"Unexpected tree kind.\");");
    out.println("    }"); // switch
    out.println("  }");   // transformAny
  }

  private static void printVisitor(String headerFileName, ArrayList<TreeInfo> trees, PrintStream out) {
    printHeader(out, headerFileName);
    printVisitAny(out, trees);
    printTreeVisits(out, trees);
    out.println("}"); // class
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
        if (isParseTreeType(field)) {
          out.printf("    visitAny(tree.%s);\n", field.getName());
        } else {
          if (isParseTreeListType(field)) {
            out.printf("    visitList(tree.%s);\n", field.getName());
          }
        }
      }
      out.println("  }");
    }
  }

  private static boolean isParseTreeListType(Field field) {
    return ImmutableList.class.isAssignableFrom((field.getType()))
        && isParseTreeType((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
  }

  private static boolean isParseTreeType(Field field) {
    return isParseTreeType(field.getType());
  }

  private static boolean isParseTreeType(Class<?> fieldType) {
    return ParseTree.class.isAssignableFrom(fieldType);
  }

  private static void printVisitAny(PrintStream out, ArrayList<TreeInfo> trees) {
    for (TreeInfo tree: trees) {
      out.printf("    case %s:\n", tree.kind.name());
      out.printf("      visit(tree.%s());\n", tree.asName);
      out.println("      break;");
    }
    out.println("    }"); //switch
    out.println("  }");   // transformAny
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
        result.add(info);
      } catch (ClassNotFoundException e) {
        System.out.printf("Missing Class Definition: %s\n", fullClassName);
      }
      // TODO: Validate
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
