package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import java.util.LinkedHashMap;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.Test;

public class SignSubmissionJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/sign_submission_juel_function_activity.bpmn")
  public void testSignSubmissionFunctionWithActivityId() {
    var taskDefinitionKey = "signSubmissionTaskKey";
    var processDefinitionKey = "testSignSubmissionKey";
    var signature = "test signature";
    var data = new LinkedHashMap<String, Object>();
    data.put("userName", "testuser");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey);

    var cephKey = cephKeyProvider().generateKey(taskDefinitionKey, processInstance.getId());
    formDataCephService().putFormData(cephKey, FormDataDto.builder().data(data).signature(signature).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = "bpmn/sign_submission_juel_function_event.bpmn")
  public void testSignSubmissionFunctionWithEventId() {
    var taskDefinitionKey = "signSubmissionTaskKey2";
    var processDefinitionKey = "testSignSubmissionKey2";
    var signature = "test signature";
    var data = new LinkedHashMap<String, Object>();
    data.put("userName", "testuser");
    Map<String, Object> vars = Map.of("start_form_ceph_key", "testSignatureDocumentId");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey, vars);

    formDataCephService().putFormData("testSignatureDocumentId", FormDataDto.builder().data(data).signature(signature).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}
