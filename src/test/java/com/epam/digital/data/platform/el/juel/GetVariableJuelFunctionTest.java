package com.epam.digital.data.platform.el.juel;

import static org.assertj.core.api.Assertions.assertThat;
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
class GetVariableJuelFunctionTest {

  @InjectMocks
  private GetVariableJuelFunction getVariableJuelFunction;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private ExecutionEntity execution;
  @Mock
  private VariableAccessor variableAccessor;

  @Test
  void getVariable() {
    var variableName = "varName";
    var variableValue = "varValue";

    Context.setExecutionContext(execution);
    getVariableJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(execution)).thenReturn(variableAccessor);
    when(variableAccessor.getVariable(variableName)).thenReturn(variableValue);

    var result = GetVariableJuelFunction.get_variable(variableName);

    assertThat(result).isSameAs(variableValue);
  }
}
