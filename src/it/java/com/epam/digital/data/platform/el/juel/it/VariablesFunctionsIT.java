package com.epam.digital.data.platform.el.juel.it;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Test;

class VariablesFunctionsIT extends BaseIT {

  @Test
  @Deployment(resources = "bpmn/variable_functions_process.bpmn")
  void testFunctionsVariables() {
    var processDefinitionKey = "variable_functions_process";
    var taskDefinitionKey = "userTask";

    var processInstance = runtimeService().startProcessInstanceByKey(processDefinitionKey);

    var taskId = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey).singleResult()
        .getId();
    taskService().complete(taskId);

    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}
