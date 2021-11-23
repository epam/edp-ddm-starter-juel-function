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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class SetVariableJuelFunctionTest {

  @InjectMocks
  private SetVariableJuelFunction setVariableJuelFunction;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private ExecutionEntity execution;
  @Mock
  private VariableAccessor variableAccessor;

  @Test
  void setVariable() {
    var variableName = "varName";
    var variableValue = "varValue";

    Context.setExecutionContext(execution);
    setVariableJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(execution)).thenReturn(variableAccessor);

    SetVariableJuelFunction.set_variable(variableName, variableValue);

    verify(variableAccessor).removeVariable(variableName);
    verify(variableAccessor).setVariable(variableName, variableValue);
  }
}
