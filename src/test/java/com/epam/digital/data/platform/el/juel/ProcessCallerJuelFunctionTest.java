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
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.dataaccessor.named.NamedVariableReadAccessor;
import com.epam.digital.data.platform.dataaccessor.sysvar.CallerProcessInstanceIdVariable;
import com.epam.digital.data.platform.el.juel.dto.ProcessCallerDto;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class ProcessCallerJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @Mock
  private CallerProcessInstanceIdVariable callerProcessInstanceIdVariable;
  @Mock
  private NamedVariableReadAccessor<String> callerProcessInstanceIdVariableReadAccessor;

  @InjectMocks
  private ProcessCallerJuelFunction processCallerJuelFunction;

  @BeforeEach
  void setUp() {
    Context.setExecutionContext(executionEntity);
    processCallerJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);
  }

  @Test
  void shouldGetExistedProcessCaller() {
    var expectedProcessCaller = ProcessCallerDto.builder()
        .id("id").build();
    when(variableAccessor.getVariable("process-caller-juel-function-result-object"))
        .thenReturn(expectedProcessCaller);

    var actualProcessCaller = ProcessCallerJuelFunction.process_caller();

    assertSame(expectedProcessCaller, actualProcessCaller);
  }

  @Test
  void shouldGetProcessCallerFromVariable() {
    var expectedProcessCallerId = "processCallerId";
    when(applicationContext.getBean(CallerProcessInstanceIdVariable.class))
        .thenReturn(callerProcessInstanceIdVariable);
    when(callerProcessInstanceIdVariable.from(executionEntity))
        .thenReturn(callerProcessInstanceIdVariableReadAccessor);
    when(callerProcessInstanceIdVariableReadAccessor.get()).thenReturn(expectedProcessCallerId);

    var actualProcessCaller = ProcessCallerJuelFunction.process_caller();

    assertThat(actualProcessCaller.getId()).isEqualTo(expectedProcessCallerId);
  }
}
