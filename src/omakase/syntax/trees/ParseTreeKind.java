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

package omakase.syntax.trees;

/**
 *
 */
public enum ParseTreeKind {
  // Omakase
  BINARY_EXPRESSION,
  BLOCK,
  CALL_EXPRESSION,
  CLASS_DECLARATION,
  EXPRESSION_STATEMENT,
  FORMAL_PARAMETER_LIST,
  FUNCTION_EXPRESSION,
  LITERAL_EXPRESSION,
  METHOD_DECLARATION,
  PARAMETER_DECLARATION,
  PAREN_EXPRESSION,
  SIMPLE_NAME_EXPRESSION,
  SOURCE_FILE,

  // Javascript
  JAVASCRIPT_BINARY_EXPRESSION,
  JAVASCRIPT_BLOCK,
  JAVASCRIPT_CALL_EXPRESSION,
  JAVASCRIPT_EXPRESSION_STATEMENT,
  JAVASCRIPT_FORMAL_PARAMETER_LIST,
  JAVASCRIPT_FUNCTION_EXPRESSION,
  JAVASCRIPT_PAREN_EXPRESSION,
  JAVASCRIPT_PROGRAM,
  JAVASCRIPT_SIMPLE_NAME_EXPRESSION
}
