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

package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import java.util.LinkedHashMap;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class SubmissionJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/submission_juel_function_activity.bpmn")
  void testSubmissionFunctionWithActivityId() {
    var taskDefinitionKey = "submissionTaskKey";
    var processDefinitionKey = "testSubmissionKey";
    var formData = new LinkedHashMap<String, Object>();
    formData.put("userName", "testuser");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey);

    formDataStorageService().putFormData(taskDefinitionKey, processInstance.getId(),
        FormDataDto.builder().data(formData).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = "bpmn/submission_juel_function_event.bpmn")
  void testSubmissionFunctionWithEventId() {
    var taskDefinitionKey = "submissionTaskKey2";
    var processDefinitionKey = "testSubmissionKey2";
    var formData = new LinkedHashMap<String, Object>();
    formData.put("userName", "testuser");
    Map<String, Object> vars = Map.of("start_form_ceph_key", "testKey");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey, vars);

    formDataStorageService().putFormData("testKey", FormDataDto.builder().data(formData).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}

