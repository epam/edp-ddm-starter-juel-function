package com.epam.digital.data.platform.el.juel;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.dataaccessor.initiator.InitiatorVariablesAccessor;
import com.epam.digital.data.platform.dataaccessor.initiator.InitiatorVariablesReadAccessor;
import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import java.util.Optional;
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
class InitiatorJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private TokenParser parser;
  @Mock
  private JwtClaimsDto jwtClaimsDto;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @Mock
  private InitiatorVariablesAccessor initiatorVariablesAccessor;
  @Mock
  private InitiatorVariablesReadAccessor initiatorVariablesReadAccessor;
  @InjectMocks
  private InitiatorJuelFunction initiatorJuelFunction;

  @BeforeEach
  void setUp() {
    Context.setExecutionContext(executionEntity);
    initiatorJuelFunction.setApplicationContext(applicationContext);

    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);
    lenient().when(applicationContext.getBean(InitiatorVariablesAccessor.class))
        .thenReturn(initiatorVariablesAccessor);
    lenient().when(initiatorVariablesAccessor.from(executionEntity))
        .thenReturn(initiatorVariablesReadAccessor);
  }

  @Test
  void existedInitiator() {
    var expect = new UserDto("userDto", null, null);
    when(variableAccessor.getVariable("initiator-juel-function-result-object")).thenReturn(expect);

    var result = InitiatorJuelFunction.initiator();

    assertSame(expect, result);
  }

  @Test
  void initiatorNoToken() {
    var expectName = "userDto";
    when(initiatorVariablesReadAccessor.getInitiatorName()).thenReturn(Optional.of(expectName));

    var result = InitiatorJuelFunction.initiator();

    assertSame(expectName, result.getUserName());
  }

  @Test
  void initiatorWithToken() {
    var expectName = "userDto";
    var token = "token";
    var fullName = "fullName";
    when(applicationContext.getBean(TokenParser.class)).thenReturn(parser);
    when(initiatorVariablesReadAccessor.getInitiatorName()).thenReturn(Optional.of(expectName));
    when(initiatorVariablesReadAccessor.getInitiatorAccessToken()).thenReturn(Optional.of(token));
    when(parser.parseClaims(token)).thenReturn(jwtClaimsDto);
    when(jwtClaimsDto.getFullName()).thenReturn(fullName);

    var result = InitiatorJuelFunction.initiator();

    assertSame(expectName, result.getUserName());
    assertSame(token, result.getAccessToken());
    assertSame(fullName, result.getFullName());
  }
}
