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

import com.epam.digital.data.platform.el.juel.dto.SignUserFormDataDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataWrapperDto;
import org.camunda.spin.Spin;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that retrieves form data object
 *
 * @see SignSubmissionJuelFunction#sign_submission(String) The function itself
 */
@Component
public class SignSubmissionJuelFunction extends AbstractSubmissionJuelFunction {

  private static final String SIGN_SUBMISSION_FUNCTION_NAME = "sign_submission";
  private static final String SIGN_SUBMISSION_OBJ_VAR_NAME_FORMAT = "sign_submission-juel-function-result-object-%s";

  public SignSubmissionJuelFunction() {
    super(SIGN_SUBMISSION_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that retrieves form data and signature from storage
   * <p>
   * Checks if there already is an object with form data and signature in Camunda execution context
   * and returns it if it exists or else reads data from storage
   *
   * @param bpmnElementId event or activity identifier
   * @return form data and signature {@link SignUserFormDataDto} representation
   */
  public static SignUserFormDataDto sign_submission(String bpmnElementId) {
    final var execution = getExecution();
    final var variableAccessor = getVariableAccessor();

    var signSubmissionResultObjectName = String
        .format(SIGN_SUBMISSION_OBJ_VAR_NAME_FORMAT, bpmnElementId);
    SignUserFormDataDto storedObject = variableAccessor.getVariable(signSubmissionResultObjectName);
    if (storedObject != null) {
      return storedObject;
    }

    var formData = getFormDataFromStorageWithKey(bpmnElementId, execution);

    var data = formData.map(FormDataWrapperDto::getFormData)
        .map(FormDataDto::getData).map(Spin::JSON).orElse(null);
    var signature = formData.map(FormDataWrapperDto::getFormData)
        .map(FormDataDto::getSignature).orElse(null);
    var signUserFormDataDto = new SignUserFormDataDto(data, signature,
        formData.isEmpty() ? null : formData.get().getStorageKey());

    variableAccessor.removeVariable(signSubmissionResultObjectName);
    variableAccessor.setVariableTransient(signSubmissionResultObjectName, signUserFormDataDto);
    return signUserFormDataDto;
  }
}
