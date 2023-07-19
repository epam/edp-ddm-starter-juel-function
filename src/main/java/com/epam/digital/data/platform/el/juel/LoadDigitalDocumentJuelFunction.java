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

package com.epam.digital.data.platform.el.juel;

import static com.epam.digital.data.platform.el.juel.util.Utils.createHeaders;

import com.epam.digital.data.platform.dgtldcmnt.client.DigitalDocumentServiceInternalApiRestClient;
import com.epam.digital.data.platform.el.juel.exception.DocumentReadBytesException;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that downloads digital document
 *
 * @see SaveDigitalDocumentFromUrlJuelFunction#save_digital_document_from_url(String, String) The
 * function itself
 */
@Component
public class LoadDigitalDocumentJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "load_digital_document";

  protected LoadDigitalDocumentJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that downloads digital document from internal API
   *
   * @param documentId  id of the document to download
   */
  public static byte[] load_digital_document(String documentId) {
    var restClient = getBean(DigitalDocumentServiceInternalApiRestClient.class);
    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();

    final var execution = getExecution();
    final var rootProcessInstanceId = execution.getRootProcessInstanceId();

    var response = restClient.download(rootProcessInstanceId, documentId,
        createHeaders(accessToken));

    return toByteArray(response);
  }

  private static byte[] toByteArray(ResponseEntity<Resource> response) {
    try {
      return response.getBody().getInputStream().readAllBytes();
    } catch (IOException e) {
      throw new DocumentReadBytesException("Unable to read response body", e);
    }
  }
}
