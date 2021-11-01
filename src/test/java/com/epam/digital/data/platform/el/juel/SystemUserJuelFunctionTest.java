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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class SystemUserJuelFunctionTest {

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
  public void shouldReturnSystemUserFromCamundaContext() {
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
