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

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import java.io.IOException;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;

class SaveDigitalDocumentJuelFunctionIT extends AbstractDigitalDocumentJuelFunctionIT {

  @Test
  @Deployment(resources = "bpmn/save_digital_document.bpmn")
  void shouldReturnCorrectDocumentMetadata() throws IOException {
    mockConnectToKeycloak();
    mockConnectToDocumentService();

    var processInstance = runtimeService().startProcessInstanceByKey("save_digital_document");

    assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = "bpmn/save_digital_document_for_root_process.bpmn")
  void shouldReturnCorrectDocumentMetadataForRootProcess() throws IOException {
    mockConnectToKeycloak();
    mockConnectToDocumentService();

    var processInstance = runtimeService().startProcessInstanceByKey(
        "root_process_with_file_saving");

    assertThat(processInstance).isEnded();

    var rootProcessInstanceId = processInstance.getId();

    digitalDocumentService.verify(
        postRequestedFor(urlPathEqualTo("/internal-api/v2/documents/" + rootProcessInstanceId))
            .withQueryParam("filename", equalTo("file.txt"))
            .withRequestBodyPart(aMultipart("file.txt")
                .withHeader("Content-Type", equalTo("text/plain"))
                .withBody(binaryEqualTo(new byte[]{1, 2, 3})).build()));
  }

  private void mockConnectToDocumentService() {
    digitalDocumentService.addStubMapping(
        stubFor(post(urlPathMatching("/internal-api/v2/documents/.*"))
            .withQueryParam("filename", equalTo("file.txt"))
            .withMultipartRequestBody(
                aMultipart("file.txt")
                    .withHeader("Content-Type", equalTo("text/plain"))
                    .withBody(binaryEqualTo(new byte[]{1, 2, 3})))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody("{\"id\": \"11111111-1111-1111-1111-111111111111\",\n"
                    + "\"name\": \"file.txt\",\n"
                    + "\"type\": \"text/plain\",\n"
                    + "\"checksum\": \"1234567890\",\n"
                    + "\"size\": 3 }"))));
  }
}