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

import omakase.syntax.trees.ParseTree;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class Project {
  private final Map<String, SourceFile> files = new LinkedHashMap<String, SourceFile>();
  private final Map<SourceFile, ParseTree> trees = new LinkedHashMap<SourceFile, ParseTree>();
  private final ErrorReporter reporter;

  public Project(ErrorReporter reporter) {

    this.reporter = reporter;
  }

  public void addFile(SourceFile file) {
    files.put(file.name, file);
  }

  public SourceFile getFile(String fileName) {
    return files.get(fileName);
  }

  public Iterable<SourceFile> files() {
    return files.values();
  }

  public ErrorReporter errorReporter() {
    return reporter;
  }

  public void setParseTree(SourceFile file, ParseTree tree) {
    trees.put(file, tree);
  }

  public Iterable<? extends ParseTree> trees() {
    return trees.values();
  }
}
