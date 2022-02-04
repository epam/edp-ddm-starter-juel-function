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

import com.epam.digital.data.platform.dataaccessor.sysvar.CallerProcessInstanceIdVariable;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class ProcessCallerJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/process_caller_juel_function.bpmn")
  void testProcessCallerFunction() {
    var messageName = "processCallerMessage";

    Map<String, Object> vars = Map.of(
        CallerProcessInstanceIdVariable.CALLER_PROCESS_INSTANCE_ID_VARIABLE_NAME,
        "testId");

    var processInstance = runtimeService().startProcessInstanceByMessage(messageName, vars);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}

