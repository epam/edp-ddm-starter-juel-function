/*
 * Copyright 2021 EPAM Systems.
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

import com.epam.digital.data.platform.bpms.api.dto.enums.PlatformHttpHeader;
import com.epam.digital.data.platform.dgtldcmnt.client.DigitalDocumentServiceInternalApiRestClient;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentDto;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentMetadataDto;
import com.epam.digital.data.platform.el.juel.dto.DocumentMetadata;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that gains access to a context variables
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
   * Static JUEL function that removes a variable (to prevent exceptions in case of changing
   * transient flag of the variable) and sets new value to an execution context
   *
   * @param remoteFileUrl  URL from which you want to download file
   * @param targetFileName new file name for this file
   */
  public static DocumentMetadata save_digital_document_from_url(String remoteFileUrl,
      String targetFileName) {

    var restClient = getBean(DigitalDocumentServiceInternalApiRestClient.class);
    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();

    final var execution = getExecution();
    final var processInstanceId = execution.getProcessInstanceId();

    URL remoteFileLocation;
    try {
      remoteFileLocation = new URL(remoteFileUrl);
    } catch (MalformedURLException e) {
      return DocumentMetadata.builder().build();
    }
    var remoteDocumentDto = RemoteDocumentDto.builder()
        .remoteFileLocation(remoteFileLocation)
        .filename(targetFileName)
        .build();
    var metadataDto = restClient.upload(processInstanceId, remoteDocumentDto,
        createHeaders(accessToken));

    return toDocumentMetadata(metadataDto);
  }

  private static HttpHeaders createHeaders(String accessToken) {
    var headers = new HttpHeaders();
    headers.add(PlatformHttpHeader.X_ACCESS_TOKEN.getName(), accessToken);
    var requestXsrfToken = UUID.randomUUID().toString();
    headers.add(PlatformHttpHeader.X_XSRF_TOKEN.getName(), requestXsrfToken);
    headers.add("Cookie", String.format("%s=%s", "XSRF-TOKEN", requestXsrfToken));
    return headers;
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
