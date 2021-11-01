package com.epam.digital.data.platform.el.juel.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class SystemUserJuelFunctionIT extends BaseIT {

  @Autowired
  @Qualifier("keycloakMockServer")
  protected WireMockServer keycloakMockServer;

  @Test
  void testSystemUserJuelFunction() throws IOException {
    mockConnectToKeycloak();

    var processInstance = runtimeService().startProcessInstanceByKey("test_system_user", "");
    assertThat(processInstance).isEnded();
  }

  protected void mockConnectToKeycloak() throws IOException {
    keycloakMockServer.addStubMapping(
        stubFor(post(urlPathEqualTo("/auth/realms/test-realm/protocol/openid-connect/token"))
            .withRequestBody(equalTo("grant_type=client_credentials"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody(getContentFromFile("/json/keycloakConnectResponse.json")))));
  }
}
