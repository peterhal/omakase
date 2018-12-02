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
  CLASS_DECLARATION,
  FUNCTION_DECLARATION,
  METHOD_DECLARATION,
  FIELD_DECLARATION,
  PARAMETER_DECLARATION,
  SOURCE_FILE,

  // Types
  ARRAY_TYPE,
  FUNCTION_TYPE,
  KEYWORD_TYPE,
  NAMED_TYPE,
  NULLABLE_TYPE,
  TYPE_ARGUMENT_LIST,

  // Expressions
  ARGUMENTS,
  ARRAY_ACCESS_EXPRESSION,
  ARRAY_LITERAL_EXPRESSION,
  BINARY_EXPRESSION,
  CALL_EXPRESSION,
  CONDITIONAL_EXPRESSION,
  PAREN_EXPRESSION,
  FUNCTION_EXPRESSION,
  IDENTIFIER_EXPRESSION,
  LITERAL_EXPRESSION,
  MEMBER_EXPRESSION,
  NEW_EXPRESSION,
  POSTFIX_EXPRESSION,
  THIS_EXPRESSION,
  UNARY_EXPRESSION,

  // Statements
  BLOCK,
  BREAK_STATEMENT,
  CASE_CLAUSE,
  CATCH_CLAUSE,
  CONTINUE_STATEMENT,
  DEBUGGER_STATEMENT,
  DEFAULT_CLAUSE,
  DO_STATEMENT,
  EMPTY_STATEMENT,
  EXPRESSION_STATEMENT,
  FORMAL_PARAMETER_LIST,
  FOR_IN_STATEMENT,
  FOR_STATEMENT,
  IF_STATEMENT,
  RETURN_STATEMENT,
  SWITCH_STATEMENT,
  THROW_STATEMENT,
  TRY_STATEMENT,
  VARIABLE_DECLARATION,
  VARIABLE_STATEMENT,
  WHILE_STATEMENT,

  // Javascript
  JAVASCRIPT_ARGUMENTS,
  JAVASCRIPT_ARRAY_ACCESS_EXPRESSION,
  JAVASCRIPT_ARRAY_LITERAL_EXPRESSION,
  JAVASCRIPT_BINARY_EXPRESSION,
  JAVASCRIPT_BLOCK,
  JAVASCRIPT_BREAK_STATEMENT,
  JAVASCRIPT_CALL_EXPRESSION,
  JAVASCRIPT_CASE_CLAUSE,
  JAVASCRIPT_CATCH_CLAUSE,
  JAVASCRIPT_COMMA_EXPRESSION,
  JAVASCRIPT_CONDITIONAL_EXPRESSION,
  JAVASCRIPT_CONTINUE_STATEMENT,
  JAVASCRIPT_DEBUGGER_STATEMENT,
  JAVASCRIPT_DEFAULT_CLAUSE,
  JAVASCRIPT_DO_STATEMENT,
  JAVASCRIPT_ELISION,
  JAVASCRIPT_EMPTY_STATEMENT,
  JAVASCRIPT_EXPRESSION_STATEMENT,
  JAVASCRIPT_FORMAL_PARAMETER_LIST,
  JAVASCRIPT_FOR_IN_STATEMENT,
  JAVASCRIPT_FOR_STATEMENT,
  JAVASCRIPT_FUNCTION_EXPRESSION,
  JAVASCRIPT_GET_ACCESSOR,
  JAVASCRIPT_IDENTIFIER_EXPRESSION,
  JAVASCRIPT_IF_STATEMENT,
  JAVASCRIPT_LABELLED_STATEMENT,
  JAVASCRIPT_LITERAL_EXPRESSION,
  JAVASCRIPT_MEMBER_EXPRESSION,
  JAVASCRIPT_NEW_EXPRESSION,
  JAVASCRIPT_OBJECT_LITERAL_EXPRESSION,
  JAVASCRIPT_PAREN_EXPRESSION,
  JAVASCRIPT_POSTFIX_EXPRESSION,
  JAVASCRIPT_PROGRAM,
  JAVASCRIPT_PROPERTY_ASSIGNMENT,
  JAVASCRIPT_RETURN_STATEMENT,
  JAVASCRIPT_SET_ACCESSOR,
  JAVASCRIPT_SWITCH_STATEMENT,
  JAVASCRIPT_THIS_EXPRESSION,
  JAVASCRIPT_THROW_STATEMENT,
  JAVASCRIPT_TRY_STATEMENT,
  JAVASCRIPT_UNARY_EXPRESSION,
  JAVASCRIPT_VARIABLE_DECLARATION,
  JAVASCRIPT_VARIABLE_STATEMENT,
  JAVASCRIPT_WHILE_STATEMENT,
  JAVASCRIPT_WITH_STATEMENT,
 }
