package com.epam.digital.data.platform.el.juel;

import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that gains access to a context variables
 *
 * @see GetVariableJuelFunction#get_variable(String) The function itself
 */
@Component
public class GetVariableJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "get_variable";

  public GetVariableJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that reads a variable from an execution context
   *
   * @param variableName name of the variable
   * @return nullable value of the variable
   */
  public static Object get_variable(String variableName) {
    final var variableAccessor = getVariableAccessor();
    return variableAccessor.getVariable(variableName);
  }
}
