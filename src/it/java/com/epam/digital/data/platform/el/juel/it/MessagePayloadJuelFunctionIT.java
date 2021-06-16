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

import com.epam.digital.data.platform.dataaccessor.sysvar.StartMessagePayloadStorageKeyVariable;
import com.epam.digital.data.platform.storage.message.dto.MessagePayloadDto;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class MessagePayloadJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/message_payload_juel_function_start_event.bpmn")
  void testMessagePayloadFunctionWithEventId() {
    var messageName = "startEventMessagePayloadMessage";
    var processDefinitionKey = "testStartEventMessagePayloadKey";
    var uuid = "randomUUID";
    var messagePayloadDto = MessagePayloadDto.builder().data(Map.of("userName", "testuser"))
        .build();
    var messageStorageKey = messagePayloadStorageService()
        .putStartMessagePayload(processDefinitionKey, uuid, messagePayloadDto);

    Map<String, Object> vars = Map.of(
        StartMessagePayloadStorageKeyVariable.START_MESSAGE_PAYLOAD_STORAGE_KEY_VARIABLE_NAME,
        messageStorageKey);

    var processInstance = runtimeService().startProcessInstanceByMessage(messageName, vars);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}

