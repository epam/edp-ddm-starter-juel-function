package com.epam.digital.data.platform.el.juel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.dataaccessor.completer.CompleterVariablesAccessor;
import com.epam.digital.data.platform.dataaccessor.completer.CompleterVariablesReadAccessor;
import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import com.epam.digital.data.platform.integration.ceph.service.impl.FormDataCephServiceImpl;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import java.util.Optional;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class CompleterJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private TokenParser parser;
  @Mock
  private CephKeyProvider cephKeyProvider;
  @Mock
  private FormDataCephServiceImpl formDataCephService;
  @Mock
  private JwtClaimsDto jwtClaimsDto;
  @Mock
  private FormDataDto formDataDto;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @Mock
  private CompleterVariablesAccessor completerVariablesAccessor;
  @Mock
  private CompleterVariablesReadAccessor completerVariablesReadAccessor;
  @InjectMocks
  private CompleterJuelFunction completerJuelFunction;

  @Before
  public void setUp() {
    Context.setExecutionContext(executionEntity);
    completerJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(TokenParser.class)).thenReturn(parser);
    when(applicationContext.getBean(CephKeyProvider.class)).thenReturn(cephKeyProvider);
    when(applicationContext.getBean(FormDataCephService.class)).thenReturn(formDataCephService);
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);
    when(applicationContext.getBean(CompleterVariablesAccessor.class)).thenReturn(
        completerVariablesAccessor);
    when(completerVariablesAccessor.from(executionEntity)).thenReturn(
        completerVariablesReadAccessor);
  }

  @Test
  public void shouldGetExistedCompleter() {
    var expect = new UserDto("testUser", null, null);
    when(variableAccessor.getVariable("completer-juel-function-result-object-test"))
        .thenReturn(expect);

    var result = CompleterJuelFunction.completer("test");

    assertSame(expect, result);
  }

  @Test
  public void shouldGetCompleterFromVarNameAndCephAccessToken() {
    var cephKey = "cephKey";
    var accessToken = "testToken";
    var preferredName = "userName";
    var taskDefinitionKey = "taskDefinitionKey";
    var processInstanceId = "processInstanceId";
    when(completerVariablesReadAccessor.getTaskCompleter(taskDefinitionKey))
        .thenReturn(Optional.of(preferredName));
    when(completerVariablesReadAccessor.getTaskCompleterToken(taskDefinitionKey))
        .thenReturn(Optional.empty());
    when(executionEntity.getProcessInstanceId()).thenReturn(processInstanceId);
    when(cephKeyProvider.generateKey(taskDefinitionKey, processInstanceId)).thenReturn(cephKey);
    when(formDataCephService.getFormData(cephKey)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getAccessToken()).thenReturn(accessToken);
    when(parser.parseClaims(accessToken)).thenReturn(jwtClaimsDto);

    var actualUserDto = CompleterJuelFunction.completer(taskDefinitionKey);

    assertThat(actualUserDto.getUserName()).isEqualTo(preferredName);
    assertThat(actualUserDto.getAccessToken()).isEqualTo(accessToken);
  }

  @Test
  public void shouldGetCompleterFromVarNameAndVarAccessToken() {
    var accessToken = "testToken";
    var preferredName = "userName";
    var taskDefinitionKey = "taskDefinitionKey";
    when(completerVariablesReadAccessor.getTaskCompleter(taskDefinitionKey))
        .thenReturn(Optional.of(preferredName));
    when(completerVariablesReadAccessor.getTaskCompleterToken(taskDefinitionKey))
        .thenReturn(Optional.of(accessToken));

    when(parser.parseClaims(accessToken)).thenReturn(jwtClaimsDto);

    var actualUserDto = CompleterJuelFunction.completer(taskDefinitionKey);

    assertThat(actualUserDto.getUserName()).isEqualTo(preferredName);
    assertThat(actualUserDto.getAccessToken()).isEqualTo(accessToken);
  }
}
