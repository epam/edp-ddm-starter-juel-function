package com.epam.digital.data.platform.el.juel;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class InitiatorJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private TokenParser parser;
  @Mock
  private ProcessDefinitionEntity processDefinition;
  @Mock
  private JwtClaimsDto jwtClaimsDto;
  @InjectMocks
  private InitiatorJuelFunction initiatorJuelFunction;

  @Before
  public void setUp() {
    Context.setExecutionContext(executionEntity);
    initiatorJuelFunction.setApplicationContext(applicationContext);

    when(applicationContext.getBean(TokenParser.class)).thenReturn(parser);
    when(executionEntity.getProcessDefinition()).thenReturn(processDefinition);
    when(processDefinition.getProperty(BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME))
        .thenReturn("initiator");
  }

  @Test
  public void existedInitiator() {
    var expect = new UserDto("userDto", null, null);
    when(executionEntity.getVariable("initiator-juel-function-result-object")).thenReturn(expect);

    var result = InitiatorJuelFunction.initiator();

    assertSame(expect, result);
  }

  @Test
  public void initiatorNoToken() {
    var expectName = "userDto";
    when(executionEntity.getVariable("initiator")).thenReturn(expectName);

    var result = InitiatorJuelFunction.initiator();

    assertSame(expectName, result.getUserName());
  }

  @Test
  public void initiatorWithToken() {
    var expectName = "userDto";
    var token = "token";
    var fullName = "fullName";
    when(executionEntity.getVariable("initiator")).thenReturn(expectName);
    when(executionEntity.getVariable("initiator_access_token")).thenReturn(token);
    when(parser.parseClaims(token)).thenReturn(jwtClaimsDto);
    when(jwtClaimsDto.getFullName()).thenReturn(fullName);

    var result = InitiatorJuelFunction.initiator();

    assertSame(expectName, result.getUserName());
    assertSame(token, result.getAccessToken());
    assertSame(fullName, result.getFullName());
  }
}
