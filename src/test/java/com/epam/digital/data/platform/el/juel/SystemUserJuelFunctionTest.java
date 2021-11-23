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

import static com.epam.digital.data.platform.el.juel.SystemUserJuelFunction.SYSTEM_USER_OBJ_VAR_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class SystemUserJuelFunctionTest {

  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @InjectMocks
  private SystemUserJuelFunction systemUserJuelFunction;

  @Test
  void shouldReturnSystemUserFromCamundaContext() {
    systemUserJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);

    Context.setExecutionContext(executionEntity);
    var username = "userName";
    var token = "token";
    var userFormCamundaContext = new UserDto(username, token, new JwtClaimsDto());

    when(variableAccessor.getVariable(SYSTEM_USER_OBJ_VAR_NAME)).thenReturn(userFormCamundaContext);

    var result = SystemUserJuelFunction.system_user();

    assertThat(result).isNotNull();
    assertThat(result.getUserName()).isEqualTo(username);
    assertThat(result.getAccessToken()).isEqualTo(token);
  }
}
