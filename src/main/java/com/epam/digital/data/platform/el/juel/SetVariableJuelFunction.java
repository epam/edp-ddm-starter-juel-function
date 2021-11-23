/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.el.juel;

import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that gains access to a context variables
 *
 * @see SetVariableJuelFunction#set_variable(String, Object) The function itself
 */
@Component
public class SetVariableJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "set_variable";

  protected SetVariableJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class, Object.class);
  }

  /**
   * Static JUEL function that removes a variable (to prevent exceptions in case of changing
   * transient flag of the variable) and sets new value to an execution context
   *
   * @param variableName  name of the variable
   * @param variableValue nullable value of the variable
   */
  public static void set_variable(String variableName, Object variableValue) {
    final var execution = getRootExecution();
    final var variableAccessor = getVariableAccessor(execution);
    variableAccessor.removeVariable(variableName);
    variableAccessor.setVariable(variableName, variableValue);
  }
}
