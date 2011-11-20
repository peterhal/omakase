// Copyright 2011 Peter Hallam
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

package omakase.util;

/**
 * A channel for reporting errors, warnings and messages to the user.
 */
public abstract class ErrorReporter {
  private boolean hadError;

  public void reportError(SourceLocation location, String format, Object... args) {
    this.hadError = true;
    String message = String.format(format, args);
    if (location != null) {
      message = String.format("%s: %s", location, message);
    }
    reportMessage(message);
  }

  public abstract void reportMessage(String message);

  public boolean hadError() {
    return hadError;
  }

  public void clearError() {
    hadError = false;
  }
}
