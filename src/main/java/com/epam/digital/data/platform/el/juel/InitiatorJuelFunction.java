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

import com.epam.digital.data.platform.dataaccessor.initiator.InitiatorVariablesAccessor;
import com.epam.digital.data.platform.dataaccessor.initiator.InitiatorVariablesReadAccessor;
import com.epam.digital.data.platform.dataaccessor.sysvar.StartFormCephKeyVariable;
import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    var initiatorAccessToken = initiatorVariablesReadAccessor.getInitiatorAccessToken();

    String token = null;
    String userName = null;
    JwtClaimsDto claims = null;
    if(initiatorAccessToken.isPresent()) {
      claims = initiatorAccessToken.map(AbstractApplicationContextAwareJuelFunction::parseClaims).orElse(null);
      token = initiatorAccessToken.get();
      userName = initiatorVariablesReadAccessor.getInitiatorName().orElse(null);
    } else {
      final var execution = getExecution();
      String initialElementId = execution.getProcessDefinition().getInitial().getId();
      var formData = getFormDataFromStorage(initialElementId, execution);
      if(formData.isPresent()) {
          token = formData.get().getAccessToken();
          claims = AbstractSubmissionJuelFunction.parseClaims(token);
          userName = claims.getPreferredUsername();
      }
    }
    UserDto userDto = new UserDto(userName, token, claims);

    variableAccessor.removeVariable(INITIATOR_OBJ_VAR_NAME);
    variableAccessor.setVariableTransient(INITIATOR_OBJ_VAR_NAME, userDto);
    return userDto;
  }

  private static InitiatorVariablesReadAccessor initiatorVariablesReadAccessor() {
    var initiatorAccessTokenVariable = getBean(InitiatorVariablesAccessor.class);
    return initiatorAccessTokenVariable.from(getExecution());
  }

  protected static Optional<FormDataDto> getFormDataFromStorage(String bpmnElementId,
                                                                ExecutionEntity execution) {
    var startEventId = execution.getProcessDefinition().getInitial().getId();
    var storageService = getBean(FormDataStorageService.class);
    if (bpmnElementId.equals(startEventId)) {
      return storageService.getFormData(getStartFormStorageKey());
    } else {
      return storageService.getFormData(bpmnElementId, execution.getProcessInstanceId());
    }
  }

  private static String getStartFormStorageKey() {
    final var execution = getExecution();
    final var startFormCephKeyVariable = getBean(StartFormCephKeyVariable.class);
    return startFormCephKeyVariable.from(execution).get();
  }
}
