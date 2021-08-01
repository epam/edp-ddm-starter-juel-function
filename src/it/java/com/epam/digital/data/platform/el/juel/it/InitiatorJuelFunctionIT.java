package com.epam.digital.data.platform.el.juel.it;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

public class InitiatorJuelFunctionIT extends BaseIT {

  @Test
  @Deployment(resources = {"bpmn/initiator_juel_function.bpmn"})
  public void initiator() {

    var processInstance = runtimeService().startProcessInstanceByKey("initiator_juel_function", "",
        Map.of("initiator", TOKEN_USER_NAME, "initiator_access_token", accessToken()));

    assertThat(processInstance).isEnded();
  }
}
