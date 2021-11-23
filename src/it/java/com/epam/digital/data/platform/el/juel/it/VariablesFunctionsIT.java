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

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class VariablesFunctionsIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/variable_functions_process.bpmn")
  void testFunctionsVariables() {
    var processDefinitionKey = "variable_functions_process";
    var taskDefinitionKey = "userTask";

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey);

    var taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey).singleResult()
        .getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}
