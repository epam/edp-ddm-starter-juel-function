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
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentDto;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentMetadataDto;
import com.epam.digital.data.platform.el.juel.dto.DocumentMetadata;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that used for saving files from remote storage in digital-document
 * service
 *
 * @see SaveDigitalDocumentFromUrlJuelFunction#save_digital_document_from_url(String, String) The
 * function itself
 */
@Component
public class SaveDigitalDocumentFromUrlJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "save_digital_document_from_url";

  protected SaveDigitalDocumentFromUrlJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class, String.class);
  }

  /**
   * Static JUEL function that sends request for downloading the file from remote location
   *
   * @param remoteFileUrl  URL from which you want to download file
   * @param targetFileName new file name for this file
   * @return stored file metadata
   */
  public static DocumentMetadata save_digital_document_from_url(String remoteFileUrl,
      String targetFileName) {
    URL remoteFileLocation;
    try {
      remoteFileLocation = new URL(remoteFileUrl);
    } catch (MalformedURLException e) {
      return DocumentMetadata.builder().build();
    }

    var restClient = getBean(DigitalDocumentServiceInternalApiRestClient.class);
    var rootProcessInstanceId = getExecution().getRootProcessInstanceId();
    var remoteDocumentDto = RemoteDocumentDto.builder()
        .remoteFileLocation(remoteFileLocation)
        .filename(targetFileName)
        .build();
    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();
    var headers = createHeaders(accessToken);
    var metadataDto = restClient.upload(rootProcessInstanceId, remoteDocumentDto, headers);

    return toDocumentMetadata(metadataDto);
  }

  private static DocumentMetadata toDocumentMetadata(RemoteDocumentMetadataDto metadataDto) {
    return DocumentMetadata.builder()
        .id(metadataDto.getId())
        .name(metadataDto.getName())
        .type(metadataDto.getType())
        .checksum(metadataDto.getChecksum())
        .size(metadataDto.getSize())
        .build();
  }
}
