package com.epam.digital.data.platform.el.juel.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class SaveDigitalDocumentFromUrlJuelFunctionIT extends BaseIT {

  @Autowired
  @Qualifier("keycloakMockServer")
  protected WireMockServer keycloak;
  @Autowired
  @Qualifier("digitalDocumentServiceMockServer")
  protected WireMockServer digitalDocumentService;

  @Test
  @Deployment(resources = "bpmn/save_digital_document_from_url.bpmn")
  void shouldReturnCorrectDocumentMetadata() throws IOException {
    mockConnectToKeycloak();
    mockConnectToDocumentService();

    var processInstance = runtimeService().startProcessInstanceByKey(
        "save_digital_document_from_url", Map.of());

    assertThat(processInstance).isEnded();
  }

  private void mockConnectToKeycloak() throws IOException {
    keycloak.addStubMapping(
        stubFor(post(urlPathEqualTo("/auth/realms/test-realm/protocol/openid-connect/token"))
            .withRequestBody(equalTo("grant_type=client_credentials"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody(getContentFromFile("/json/keycloakConnectResponse.json")))));
  }

  private void mockConnectToDocumentService() {
    digitalDocumentService.addStubMapping(
        stubFor(post(urlPathMatching("/internal-api/documents/.*"))
            .withRequestBody(equalTo("{\"remoteFileLocation\":\"http://some.url.com/image.png\","
                + "\"filename\":\"newImage.png\"}"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody("{\"id\": \"11111111-1111-1111-1111-111111111111\",\n"
                    + "\"name\": \"newImage.png\",\n"
                    + "\"type\": \"image/png\",\n"
                    + "\"checksum\": \"1234567890\",\n"
                    + "\"size\": 10000 }"))));
  }
}