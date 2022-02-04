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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.dataaccessor.named.NamedVariableReadAccessor;
import com.epam.digital.data.platform.dataaccessor.sysvar.StartMessagePayloadStorageKeyVariable;
import com.epam.digital.data.platform.el.juel.dto.MessagePayloadReadOnlyDto;
import com.epam.digital.data.platform.storage.message.dto.MessagePayloadDto;
import com.epam.digital.data.platform.storage.message.service.MessagePayloadStorageService;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class MessagePayloadJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private MessagePayloadStorageService messagePayloadStorageService;
  @Mock
  private ProcessDefinitionEntity processDefinitionEntity;
  @Mock
  private ActivityImpl activity;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @Mock
  private StartMessagePayloadStorageKeyVariable startMessagePayloadStorageKey;
  @Mock
  private NamedVariableReadAccessor<String> startMessagePayloadCephKeyReadAccessor;

  @InjectMocks
  private MessagePayloadJuelFunction messagePayloadJuelFunction;

  @BeforeEach
  void setUp() {
    Context.setExecutionContext(executionEntity);
    messagePayloadJuelFunction.setApplicationContext(applicationContext);
    lenient().when(executionEntity.getProcessDefinition()).thenReturn(processDefinitionEntity);
    lenient().when(processDefinitionEntity.getInitial()).thenReturn(activity);
    lenient().when(activity.getId()).thenReturn("startEventId");
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);
  }

  @Test
  void shouldGetExistedMessagePayload() {
    var expectedMessagePayload = MessagePayloadReadOnlyDto.builder()
        .data(Map.of("userName", "testuser")).build();
    when(variableAccessor.getVariable("message-payload-juel-function-result-object-test"))
        .thenReturn(expectedMessagePayload);

    var actualMessagePayload = MessagePayloadJuelFunction.message_payload("test");

    assertSame(expectedMessagePayload, actualMessagePayload);
  }

  @Test
  void shouldGetEmptyMessagePayloadFromCeph() {
    var taskDefinitionKey = "taskDefinitionKey";
    when(applicationContext.getBean(MessagePayloadStorageService.class)).thenReturn(
        messagePayloadStorageService);

    var actualMessagePayload = MessagePayloadJuelFunction.message_payload(taskDefinitionKey);

    verify(messagePayloadStorageService, never()).getMessagePayload(any());
    assertThat(actualMessagePayload).isNotNull();
    assertThat(actualMessagePayload.getData()).isNotNull().isEmpty();
  }

  @Test
  void shouldGetStartEventMessagePayloadFromCeph() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "startEventId";
    var expectedMessagePayload = MessagePayloadDto.builder()
        .data(Map.of("userName", "testuser")).build();
    when(applicationContext.getBean(StartMessagePayloadStorageKeyVariable.class))
        .thenReturn(startMessagePayloadStorageKey);
    when(startMessagePayloadStorageKey.from(executionEntity))
        .thenReturn(startMessagePayloadCephKeyReadAccessor);
    when(startMessagePayloadCephKeyReadAccessor.get()).thenReturn(cephKey);
    when(applicationContext.getBean(MessagePayloadStorageService.class)).thenReturn(
        messagePayloadStorageService);
    when(messagePayloadStorageService.getMessagePayload(cephKey)).thenReturn(
        Optional.of(expectedMessagePayload));

    var actualMessagePayload = MessagePayloadJuelFunction.message_payload(taskDefinitionKey);

    assertThat(actualMessagePayload.getData()).isEqualTo(expectedMessagePayload.getData());
  }
}
