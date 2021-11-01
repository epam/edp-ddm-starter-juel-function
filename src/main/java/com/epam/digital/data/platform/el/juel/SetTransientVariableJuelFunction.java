package com.epam.digital.data.platform.el.juel;

import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that gains access to a context variables
 *
 * @see SetTransientVariableJuelFunction#set_transient_variable(String, Object) The function itself
 */
@Component
public class SetTransientVariableJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "set_transient_variable";

  protected SetTransientVariableJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class, Object.class);
  }

  /**
   * Static JUEL function that removes a variable (to prevent exceptions in case of changing
   * transient flag of the variable) and sets new transient value to an execution context
   *
   * @param variableName  name of the variable
   * @param variableValue nullable value of the variable
   */
  public static void set_transient_variable(String variableName, Object variableValue) {
    final var execution = getRootExecution();
    final var variableAccessor = getVariableAccessor(execution);
    variableAccessor.removeVariable(variableName);
    variableAccessor.setVariableTransient(variableName, variableValue);
  }
}
