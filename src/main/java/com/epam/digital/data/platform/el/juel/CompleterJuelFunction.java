package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.dataaccessor.completer.CompleterVariablesAccessor;
import com.epam.digital.data.platform.dataaccessor.completer.CompleterVariablesReadAccessor;
import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that resolves an completer info object
 *
 * @see CompleterJuelFunction#completer(String) The function itself
 */
@Component
public class CompleterJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "completer";
  private static final String COMPLETER_OBJ_NAME_FORMAT = "completer-juel-function-result-object-%s";

  public CompleterJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that resolves an {@link UserDto} object of the user-task completer
   * <p>
   * Checks if there already is an object with completer info in Camunda execution context and
   * returns it if it exists or else reads token from ceph, parses token claims and creates an
   * {@link UserDto} object with all found data
   *
   * @return completer {@link UserDto} representation
   */
  public static UserDto completer(String taskDefinitionKey) {
    final var variableAccessor = getVariableAccessor();

    var completerResultObjectName = String.format(COMPLETER_OBJ_NAME_FORMAT, taskDefinitionKey);
    UserDto storedObject = variableAccessor.getVariable(completerResultObjectName);

    if (Objects.nonNull(storedObject)) {
      return storedObject;
    }

    final var execution = (ExecutionEntity) getExecution();
    var userDto = createUserDto(taskDefinitionKey, execution.getProcessInstanceId());
    variableAccessor.removeVariable(completerResultObjectName);
    variableAccessor.setVariableTransient(completerResultObjectName, userDto);
    return userDto;
  }

  private static UserDto createUserDto(String taskDefinitionKey, String processInstanceId) {
    final var completerVariablesReadAccessor = completerVariablesReadAccessor();
    var varCompleterName = completerVariablesReadAccessor.getTaskCompleter(taskDefinitionKey)
        .orElse(null);
    var varCompleterAccessToken = completerVariablesReadAccessor.getTaskCompleterToken(
        taskDefinitionKey);

    UserDto userDto;
    if (varCompleterAccessToken.isPresent()) {
      var token = varCompleterAccessToken.get();
      var claims = parseClaims(token);
      userDto = new UserDto(varCompleterName, token, claims);
    } else {
      var completerAccessToken = getAccessTokenFromCeph(taskDefinitionKey, processInstanceId);
      var claims = completerAccessToken
          .map(AbstractApplicationContextAwareJuelFunction::parseClaims);
      userDto = new UserDto(varCompleterName, completerAccessToken.orElse(null),
          claims.orElse(null));
    }
    return userDto;
  }

  private static Optional<String> getAccessTokenFromCeph(String taskDefinitionKey,
      String processInstanceId) {
    var cephKeyProvider = getBean(CephKeyProvider.class);
    var cephKey = cephKeyProvider.generateKey(taskDefinitionKey, processInstanceId);
    var cephService = getBean(FormDataCephService.class);
    var formData = cephService.getFormData(cephKey);
    return formData.map(FormDataDto::getAccessToken);
  }

  private static CompleterVariablesReadAccessor completerVariablesReadAccessor() {
    final var execution = (ExecutionEntity) getExecution();
    final var completerVariablesAccessor = getBean(CompleterVariablesAccessor.class);
    return completerVariablesAccessor.from(execution);
  }
}
