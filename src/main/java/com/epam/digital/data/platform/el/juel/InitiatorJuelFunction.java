package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.dataaccessor.initiator.InitiatorVariablesAccessor;
import com.epam.digital.data.platform.dataaccessor.initiator.InitiatorVariablesReadAccessor;
import com.epam.digital.data.platform.el.juel.dto.UserDto;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that resolves an initiator info object
 *
 * @see InitiatorJuelFunction#initiator() The function itself
 */
@Component
public class InitiatorJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "initiator";
  private static final String INITIATOR_OBJ_VAR_NAME = "initiator-juel-function-result-object";

  public InitiatorJuelFunction() {
    super(JUEL_FUNCTION_NAME);
  }

  /**
   * Static JUEL function that resolves an {@link UserDto} object of the business-process initiator
   * <p>
   * Checks if there already is an object with initiator info in Camunda execution context and
   * returns it if it exists or else reads an initiator userName and token (if it still exists) from
   * initiator variables, parses token claims and creates an {@link UserDto} object with all found
   * data
   *
   * @return initiator {@link UserDto} representation
   */
  public static UserDto initiator() {
    final var variableAccessor = getVariableAccessor();

    UserDto storedObject = variableAccessor.getVariable(INITIATOR_OBJ_VAR_NAME);
    if (storedObject != null) {
      return storedObject;
    }

    final var initiatorVariablesReadAccessor = initiatorVariablesReadAccessor();

    var initiatorUserName = initiatorVariablesReadAccessor.getInitiatorName();
    var initiatorAccessToken = initiatorVariablesReadAccessor.getInitiatorAccessToken();
    var claims = initiatorAccessToken.map(AbstractApplicationContextAwareJuelFunction::parseClaims);
    var userDto = new UserDto(initiatorUserName.orElse(null), initiatorAccessToken.orElse(null),
        claims.orElse(null));

    variableAccessor.removeVariable(INITIATOR_OBJ_VAR_NAME);
    variableAccessor.setVariableTransient(INITIATOR_OBJ_VAR_NAME, userDto);
    return userDto;
  }

  private static InitiatorVariablesReadAccessor initiatorVariablesReadAccessor() {
    var initiatorAccessTokenVariable = getBean(InitiatorVariablesAccessor.class);
    return initiatorAccessTokenVariable.from((DelegateExecution) getExecution());
  }
}
