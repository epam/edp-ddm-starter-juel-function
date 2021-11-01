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
import com.epam.digital.data.platform.el.juel.dto.SignUserFormDataDto;
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
public class SignSubmissionJuelFunctionTest {

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
  private SignSubmissionJuelFunction signSubmissionJuelFunction;

  @Before
  public void setUp() {
    Context.setExecutionContext(executionEntity);
    signSubmissionJuelFunction.setApplicationContext(applicationContext);
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
    var data = JSON(Map.of("userName", "testuser"));
    var signature = "test signature";
    var signatureDocumentId = "testId";
    when(variableAccessor.getVariable("sign_submission-juel-function-result-object-test"))
        .thenReturn(new SignUserFormDataDto(data, signature, signatureDocumentId));

    var signUserFormDataDto = SignSubmissionJuelFunction.sign_submission("test");

    assertSame(data, signUserFormDataDto.getFormData());
    assertSame(signature, signUserFormDataDto.getSignature());
    assertSame(signatureDocumentId, signUserFormDataDto.getSignatureDocumentId());
  }

  @Test
  public void shouldGetUserTaskFormDataFromCeph() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "taskDefinitionKey";
    var processInstanceId = "processInstanceId";
    var signature = "test signature";
    var data = new LinkedHashMap<String, Object>();
    data.put("userName", "testuser");
    when(executionEntity.getProcessInstanceId()).thenReturn(processInstanceId);
    when(applicationContext.getBean(CephKeyProvider.class)).thenReturn(cephKeyProvider);
    when(cephKeyProvider.generateKey(taskDefinitionKey, processInstanceId)).thenReturn(cephKey);
    when(applicationContext.getBean(FormDataCephService.class)).thenReturn(formDataCephService);
    when(formDataCephService.getFormData(cephKey)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getData()).thenReturn(data);
    when(formDataDto.getSignature()).thenReturn(signature);

    var signUserFormDataDto = SignSubmissionJuelFunction.sign_submission(taskDefinitionKey);

    assertThat("testuser")
        .isEqualTo(signUserFormDataDto.getFormData().prop("userName").stringValue());
    assertThat(signature).isEqualTo(signUserFormDataDto.getSignature());
    assertThat(cephKey).isEqualTo(signUserFormDataDto.getSignatureDocumentId());
  }

  @Test
  public void shouldGetStartEventFormDataFromCeph() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "startEventId";
    var signature = "test signature";
    var data = new LinkedHashMap<String, Object>();
    data.put("userName", "testuser");
    when(startFormCephKeyReadAccessor.get()).thenReturn(cephKey);
    when(applicationContext.getBean(FormDataCephService.class)).thenReturn(formDataCephService);
    when(formDataCephService.getFormData(cephKey)).thenReturn(Optional.of(formDataDto));
    when(formDataDto.getData()).thenReturn(data);
    when(formDataDto.getSignature()).thenReturn(signature);

    var signUserFormDataDto = SignSubmissionJuelFunction.sign_submission(taskDefinitionKey);

    assertThat("testuser")
        .isEqualTo(signUserFormDataDto.getFormData().prop("userName").stringValue());
    assertThat(signature).isEqualTo(signUserFormDataDto.getSignature());
    assertThat(cephKey).isEqualTo(signUserFormDataDto.getSignatureDocumentId());
  }

  @Test
  public void shouldReturnNullCephKeyIfDocumentNotExists() {
    var cephKey = "cephKey";
    var taskDefinitionKey = "task";
    var processInstanceId = "processInstanceId";
    when(executionEntity.getProcessInstanceId()).thenReturn(processInstanceId);
    when(applicationContext.getBean(CephKeyProvider.class)).thenReturn(cephKeyProvider);
    when(cephKeyProvider.generateKey(taskDefinitionKey, processInstanceId)).thenReturn(cephKey);
    when(applicationContext.getBean(FormDataCephService.class)).thenReturn(formDataCephService);
    when(formDataCephService.getFormData(cephKey)).thenReturn(Optional.empty());

    var signUserFormDataDto = SignSubmissionJuelFunction.sign_submission(taskDefinitionKey);

    assertThat(signUserFormDataDto.getFormData()).isNull();
    assertThat(signUserFormDataDto.getSignature()).isNull();
    assertThat(signUserFormDataDto.getSignatureDocumentId()).isNull();
  }
}
