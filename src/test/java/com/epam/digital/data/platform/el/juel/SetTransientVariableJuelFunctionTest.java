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
class SetTransientVariableJuelFunctionTest {

  @InjectMocks
  private SetTransientVariableJuelFunction setTransientVariableJuelFunction;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private ExecutionEntity execution;
  @Mock
  private VariableAccessor variableAccessor;

  @Test
  void setTransientVariable() {
    var variableName = "varName";
    var variableValue = "varValue";

    Context.setExecutionContext(execution);
    setTransientVariableJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(execution)).thenReturn(variableAccessor);

    SetTransientVariableJuelFunction.set_transient_variable(variableName, variableValue);

    verify(variableAccessor).removeVariable(variableName);
    verify(variableAccessor).setVariableTransient(variableName, variableValue);
  }
}
