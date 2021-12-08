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

import com.epam.digital.data.platform.el.juel.dto.UserFormDataDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import org.camunda.spin.Spin;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that retrieves form data object
 *
 * @see SubmissionJuelFunction#submission(String) The function itself
 */
@Component
public class SubmissionJuelFunction extends AbstractSubmissionJuelFunction {

  private static final String SUBMISSION_FUNCTION_NAME = "submission";
  private static final String SUBMISSION_OBJ_VAR_NAME_FORMAT = "submission-juel-function-result-object-%s";

  public SubmissionJuelFunction() {
    super(SUBMISSION_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that retrieves form data from storage
   * <p>
   * Checks if there already is an object with form data info in Camunda execution context and
   * returns it if it exists or else reads form data from storage
   *
   * @param bpmnElementId event or activity identifier
   * @return form data {@link UserFormDataDto} representation
   */
  public static UserFormDataDto submission(String bpmnElementId) {
    final var execution = getExecution();
    final var variableAccessor = getVariableAccessor();

    var submissionResultObjectName = String
        .format(SUBMISSION_OBJ_VAR_NAME_FORMAT, bpmnElementId);
    UserFormDataDto storedObject = variableAccessor.getVariable(submissionResultObjectName);
    if (storedObject != null) {
      return storedObject;
    }

    var formData = getFormDataFromStorage(bpmnElementId, execution);
    var data = formData.map(FormDataDto::getData).map(Spin::JSON).orElse(null);
    var userFormData = new UserFormDataDto(data);

    variableAccessor.removeVariable(submissionResultObjectName);
    variableAccessor.setVariableTransient(submissionResultObjectName, userFormData);
    return userFormData;
  }
}
