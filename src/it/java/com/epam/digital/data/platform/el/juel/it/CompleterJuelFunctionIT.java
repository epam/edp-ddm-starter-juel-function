package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class CompleterJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/completer_juel_function.bpmn")
  void testCompleterFunction() {
    var taskDefinitionKey = "waitConditionTaskKey";
    var processDefinitionKey = "testCompleterKey";
    Map<String, Object> vars = Map.of(String.format("%s_completer", taskDefinitionKey), "testuser");

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey, vars);

    var cephKey = cephKeyProvider()
        .generateKey(taskDefinitionKey, processInstance.getId());
    formDataCephService().putFormData(cephKey,
        FormDataDto.builder().accessToken(accessToken()).build());

    String taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
        .singleResult().getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}
