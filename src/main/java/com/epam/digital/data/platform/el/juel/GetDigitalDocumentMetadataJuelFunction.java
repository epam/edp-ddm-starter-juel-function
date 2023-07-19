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
import com.epam.digital.data.platform.dgtldcmnt.dto.InternalApiDocumentMetadataDto;
import com.epam.digital.data.platform.el.juel.dto.DocumentMetadata;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that used for get document metadata from digital-document service
 *
 * @see GetDigitalDocumentMetadataJuelFunction#get_digital_document_metadata(String)  The function itself
 */
@Component
public class GetDigitalDocumentMetadataJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "get_digital_document_metadata";

  protected GetDigitalDocumentMetadataJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that sends request for getting digital document metadata
   *
   * @param documentId        id of digital document
   * @return stored file metadata
   */
  public static DocumentMetadata get_digital_document_metadata(String documentId) {
    var restClient = getBean(DigitalDocumentServiceInternalApiRestClient.class);
    var rootProcessInstanceId = getExecution().getRootProcessInstanceId();
    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();
    var headers = createHeaders(accessToken);
    var metadataDto = restClient.getMetadata(rootProcessInstanceId, documentId, headers);

    return toDocumentMetadata(metadataDto);
  }

  private static DocumentMetadata toDocumentMetadata(InternalApiDocumentMetadataDto metadataDto) {
    return DocumentMetadata.builder()
        .id(metadataDto.getId())
        .name(metadataDto.getName())
        .type(metadataDto.getType())
        .checksum(metadataDto.getChecksum())
        .size(metadataDto.getSize())
        .build();
  }
}