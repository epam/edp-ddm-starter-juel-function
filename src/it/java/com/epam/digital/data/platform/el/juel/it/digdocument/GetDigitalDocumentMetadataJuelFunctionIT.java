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

import java.io.IOException;
import java.util.Map;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;

class GetDigitalDocumentMetadataJuelFunctionIT extends AbstractDigitalDocumentJuelFunctionIT {

  @Test
  @Deployment(resources = "bpmn/save_digital_document_from_url.bpmn")
  void shouldReturnCorrectDocumentMetadata() throws IOException {
    mockConnectToKeycloak();
    mockConnectToDocumentService();

    var processInstance = runtimeService().startProcessInstanceByKey(
        "get_digital_document_metadata", Map.of());

    assertThat(processInstance).isEnded();
  }

  private void mockConnectToDocumentService() {
    digitalDocumentService.addStubMapping(
        stubFor(get(urlPathMatching("/internal-api/documents/.*"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody("{\"id\": \"11111111-1111-1111-1111-111111111111\",\n"
                    + "\"name\": \"file.txt\",\n"
                    + "\"type\": \"text/plain\",\n"
                    + "\"checksum\": \"1234567890\",\n"
                    + "\"size\": 3 }"))));
  }
}
