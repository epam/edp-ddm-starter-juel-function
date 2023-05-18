/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.el.juel.it.digdocument;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Map;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class LoadDigitalDocumentJuelFunctionIT extends AbstractDigitalDocumentJuelFunctionIT {

  @Autowired
  @Qualifier("keycloakMockServer")
  protected WireMockServer keycloak;
  @Autowired
  @Qualifier("digitalDocumentServiceMockServer")
  protected WireMockServer digitalDocumentService;

  @Test
  @SneakyThrows
  @Deployment(resources = "bpmn/load_digital_document.bpmn")
  void shouldLoadDigitalDocument() {
    mockConnectToKeycloak();
    mockConnectToDocumentService();

    var processInstance = runtimeService().startProcessInstanceByKey(
        "load_digital_document", Map.of());

    assertThat(processInstance).isEnded();
  }

  private void mockConnectToDocumentService() {
    digitalDocumentService.addStubMapping(
        stubFor(get(urlPathMatching("/internal-api/documents/.*"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "image/png")
                .withBody(new byte[]{1, 2, 3}))));
  }
}
