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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.camunda.spin.Spin.JSON;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.dataaccessor.named.NamedVariableReadAccessor;
import com.epam.digital.data.platform.dataaccessor.sysvar.StartFormCephKeyVariable;
import com.epam.digital.data.platform.el.juel.dto.UserFormDataDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import java.util.LinkedHashMap;
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
class SubmissionJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private FormDataStorageService formDataStorageService;
  @Mock
  private FormDataDto formDataDto;
  @Mock
  private ProcessDefinitionEntity processDefinitionEntity;
  @Mock
  private ActivityImpl activity;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @Mock
  private StartFormCephKeyVariable startFormCephKeyVariable;
  @Mock
  private NamedVariableReadAccessor<String> startFormCephKeyReadAccessor;

  @InjectMocks
  private SubmissionJuelFunction submissionJuelFunction;

  @BeforeEach
  void setUp() {
    Context.setExecutionContext(executionEntity);
    submissionJuelFunction.setApplicationContext(applicationContext);
    lenient().when(executionEntity.getProcessDefinition()).thenReturn(processDefinitionEntity);
    lenient().when(processDefinitionEntity.getInitial()).thenReturn(activity);
    lenient().when(activity.getId()).thenReturn("startEventId");
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);
  }

  @Test
  void shouldGetExistedFormData() {
    var expectedFormData = JSON(Map.of("userName", "testuser"));
    when(variableAccessor.getVariable("submission-juel-function-result-object-test"))
        .thenReturn(new UserFormDataDto(expectedFormData));

    var actualFormData = SubmissionJuelFunction.submission("test");

    assertSame(expectedFormData, actualFormData.getFormData());
  }

  @Test
  void shouldGetUserTaskFormDataFromCeph() {
    var taskDefinitionKey = "taskDefinitionKey";
    var processInstanceId = "processInstanceId";
    var expectedFormData = new LinkedHashMap<String, Object>();
    expectedFormData.put("userName", "testuser");
    when(executionEntity.getProcessInstanceId()).thenReturn(processInstanceId);
    when(applicationContext.getBean(FormDataStorageService.class)).thenReturn(formDataStorageService);
    when(formDataStorageService.getFormData(taskDefinitionKey, processInstanceId)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getData()).thenReturn(expectedFormData);

    var actualFormData = SubmissionJuelFunction.submission(taskDefinitionKey);

    assertThat("testuser").isEqualTo(actualFormData.getFormData().prop("userName").stringValue());
  }

  @Test
  void shouldGetStartEventFormDataFromCeph() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "startEventId";
    var expectedFormData = new LinkedHashMap<String, Object>();
    expectedFormData.put("userName", "testuser");
    when(applicationContext.getBean(StartFormCephKeyVariable.class))
        .thenReturn(startFormCephKeyVariable);
    when(startFormCephKeyVariable.from(executionEntity)).thenReturn(startFormCephKeyReadAccessor);
    when(startFormCephKeyReadAccessor.get()).thenReturn(cephKey);
    when(applicationContext.getBean(FormDataStorageService.class)).thenReturn(formDataStorageService);
    when(formDataStorageService.getFormData(cephKey)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getData()).thenReturn(expectedFormData);

    var actualFormData = SubmissionJuelFunction.submission(taskDefinitionKey);

    assertThat("testuser").isEqualTo(actualFormData.getFormData().prop("userName").stringValue());
  }
}
