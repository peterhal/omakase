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

import com.google.common.io.Files;
import omakase.util.ErrorReporter;
import omakase.util.SourceFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 */
public class ProjectReader {

  public static Project readProject(String[] args, ErrorReporter reporter) {
    // TODO: Use error reporter.
    if (args.length == 0) {
      System.err.println("Missing filename argument.");
      return null;
    }

    Project project = new Project(reporter);
    boolean hadError = false;
    for (String arg : args) {
      SourceFile file = readFile(arg);
      if (file == null) {
        hadError = true;
      } else {
        project.addFile(file);
      }
    }
    return hadError ? null : project;
  }

  private static SourceFile readFile(String filename) {
    String source;
    try {
      source = Files.toString(new File(filename), Charset.defaultCharset());
    } catch (IOException e) {
      System.err.format("Error '%s' attempting to open file '%s'.\n", e.toString(), filename);
      return null;
    }

    return new SourceFile(filename, source);
  }
}
