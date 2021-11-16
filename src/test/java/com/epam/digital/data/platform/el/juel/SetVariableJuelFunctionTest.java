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