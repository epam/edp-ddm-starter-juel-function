package com.epam.digital.data.platform.el.juel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.camunda.spin.Spin.JSON;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.dataaccessor.named.NamedVariableReadAccessor;
import com.epam.digital.data.platform.dataaccessor.sysvar.StartFormCephKeyVariable;
import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.el.juel.dto.UserFormDataDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import com.epam.digital.data.platform.integration.ceph.service.impl.FormDataCephServiceImpl;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionJuelFunctionTest {

  @Mock
  private ExecutionEntity executionEntity;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private CephKeyProvider cephKeyProvider;
  @Mock
  private FormDataCephServiceImpl formDataCephService;
  @Mock
  private FormDataDto formDataDto;
  @Mock
  private ProcessDefinitionEntity processDefinitionEntity;
  @Mock
  private ActivityImpl activity;
  @Mock
  private VariableAccessorFactory variableAccessorFactory;
  @Mock
  private VariableAccessor variableAccessor;
  @Mock
  private StartFormCephKeyVariable startFormCephKeyVariable;
  @Mock
  private NamedVariableReadAccessor<String> startFormCephKeyReadAccessor;

  @InjectMocks
  private SubmissionJuelFunction submissionJuelFunction;

  @Before
  public void setUp() {
    Context.setExecutionContext(executionEntity);
    submissionJuelFunction.setApplicationContext(applicationContext);
    when(executionEntity.getProcessDefinition()).thenReturn(processDefinitionEntity);
    when(processDefinitionEntity.getInitial()).thenReturn(activity);
    when(activity.getId()).thenReturn("startEventId");
    when(applicationContext.getBean(VariableAccessorFactory.class)).thenReturn(
        variableAccessorFactory);
    when(variableAccessorFactory.from(executionEntity)).thenReturn(variableAccessor);
    when(applicationContext.getBean(StartFormCephKeyVariable.class)).thenReturn(
        startFormCephKeyVariable);
    when(startFormCephKeyVariable.from(executionEntity)).thenReturn(
        startFormCephKeyReadAccessor);
  }

  @Test
  public void shouldGetExistedFormData() {
    var expectedFormData = JSON(Map.of("userName", "testuser"));
    when(variableAccessor.getVariable("submission-juel-function-result-object-test"))
        .thenReturn(new UserFormDataDto(expectedFormData));

    var actualFormData = SubmissionJuelFunction.submission("test");

    assertSame(expectedFormData, actualFormData.getFormData());
  }

  @Test
  public void shouldGetUserTaskFormDataFromCeph() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "taskDefinitionKey";
    var processInstanceId = "processInstanceId";
    var expectedFormData = new LinkedHashMap<String, Object>();
    expectedFormData.put("userName", "testuser");
    when(executionEntity.getProcessInstanceId()).thenReturn(processInstanceId);
    when(applicationContext.getBean(CephKeyProvider.class)).thenReturn(cephKeyProvider);
    when(cephKeyProvider.generateKey(taskDefinitionKey, processInstanceId)).thenReturn(cephKey);
    when(applicationContext.getBean(FormDataCephService.class)).thenReturn(formDataCephService);
    when(formDataCephService.getFormData(cephKey)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getData()).thenReturn(expectedFormData);

    var actualFormData = SubmissionJuelFunction.submission(taskDefinitionKey);

    assertThat("testuser").isEqualTo(actualFormData.getFormData().prop("userName").stringValue());
  }

  @Test
  public void shouldGetStartEventFormDataFromCeph() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "startEventId";
    var expectedFormData = new LinkedHashMap<String, Object>();
    expectedFormData.put("userName", "testuser");
    when(startFormCephKeyReadAccessor.get()).thenReturn(cephKey);
    when(applicationContext.getBean(FormDataCephService.class)).thenReturn(formDataCephService);
    when(formDataCephService.getFormData(cephKey)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getData()).thenReturn(expectedFormData);

    var actualFormData = SubmissionJuelFunction.submission(taskDefinitionKey);

    assertThat("testuser").isEqualTo(actualFormData.getFormData().prop("userName").stringValue());
  }
}
