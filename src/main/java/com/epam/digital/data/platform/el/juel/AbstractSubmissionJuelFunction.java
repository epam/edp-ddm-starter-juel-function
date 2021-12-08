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

import com.epam.digital.data.platform.dataaccessor.sysvar.StartFormCephKeyVariable;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataWrapperDto;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import java.util.Optional;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

public abstract class AbstractSubmissionJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  /**
   * Inherited constructor of AbstractSubmissionJuelFunction
   */
  protected AbstractSubmissionJuelFunction(String juelFunctionName, Class<?>... paramTypes) {
    super(juelFunctionName, paramTypes);
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

  protected static Optional<FormDataWrapperDto> getFormDataFromStorageWithKey(String bpmnElementId,
      ExecutionEntity execution) {
    var startEventId = execution.getProcessDefinition().getInitial().getId();
    var storageService = getBean(FormDataStorageService.class);
    if (bpmnElementId.equals(startEventId)) {
      return storageService.getFormDataWithKey(getStartFormStorageKey());
    } else {
      return storageService.getFormDataWithKey(bpmnElementId, execution.getProcessInstanceId());
    }
  }

  private static String getStartFormStorageKey() {
    final var execution = getExecution();
    final var startFormCephKeyVariable = getBean(StartFormCephKeyVariable.class);
    return startFormCephKeyVariable.from(execution).get();
  }
}
