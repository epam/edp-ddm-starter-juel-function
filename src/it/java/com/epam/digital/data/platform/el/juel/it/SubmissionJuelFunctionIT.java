package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import java.util.LinkedHashMap;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class SubmissionJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/submission_juel_function_activity.bpmn")
  void testSubmissionFunctionWithActivityId() {
    var taskDefinitionKey = "submissionTaskKey";
    var processDefinitionKey = "testSubmissionKey";
    var formData = new LinkedHashMap<String, Object>();
    formData.put("userName", "testuser");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey);

    var cephKey = cephKeyProvider().generateKey(taskDefinitionKey, processInstance.getId());
    formDataCephService().putFormData(cephKey, FormDataDto.builder().data(formData).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = "bpmn/submission_juel_function_event.bpmn")
  void testSubmissionFunctionWithEventId() {
    var taskDefinitionKey = "submissionTaskKey2";
    var processDefinitionKey = "testSubmissionKey2";
    var formData = new LinkedHashMap<String, Object>();
    formData.put("userName", "testuser");
    Map<String, Object> vars = Map.of("start_form_ceph_key", "testKey");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey, vars);

    formDataCephService().putFormData("testKey", FormDataDto.builder().data(formData).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}

