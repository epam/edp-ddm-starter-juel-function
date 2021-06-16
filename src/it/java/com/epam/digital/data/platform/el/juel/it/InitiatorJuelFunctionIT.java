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

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import java.util.Map;
import java.util.UUID;

import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.service.FormDataKeyProvider;
import com.epam.digital.data.platform.storage.form.service.FormDataKeyProviderImpl;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;

class InitiatorJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = {"bpmn/initiator_juel_function.bpmn"})
  void initiator() {

    var processInstance = runtimeService().startProcessInstanceByKey("initiator_juel_function", "",
        Map.of("initiator", TOKEN_USER_NAME, "initiator_access_token", accessToken()));

    assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = {"bpmn/initiator_juel_function2.bpmn"})
  void shouldReceiveAccessTokenFromForm() {
    FormDataKeyProvider d = new FormDataKeyProviderImpl();
    String processDefinitionKey = "initiator_juel_function2";
    UUID uuid = UUID.randomUUID();
    String initiatorJuelFunction = d.generateStartFormKey(processDefinitionKey, uuid.toString());
    formDataStorageService().putStartFormData(processDefinitionKey, uuid.toString(),
            FormDataDto.builder().accessToken(accessToken()).build());
    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey, "",
            Map.of("start_form_ceph_key", initiatorJuelFunction));

    assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = {"bpmn/initiator_juel_function3.bpmn"})
  void shouldReceiveAccessTokenFromContextThenFromForm() {
    FormDataKeyProvider d = new FormDataKeyProviderImpl();
    String processDefinitionKey = "initiator_juel_function3";
    UUID uuid = UUID.randomUUID();
    String initiatorJuelFunction = d.generateStartFormKey(processDefinitionKey, uuid.toString());
    formDataStorageService().putStartFormData(processDefinitionKey, uuid.toString(),
            FormDataDto.builder().accessToken(accessToken()).build());
    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey, "",
            Map.of("start_form_ceph_key", initiatorJuelFunction, "initiator_access_token", accessToken()));

    assertThat(processInstance).isEnded();
  }
}
