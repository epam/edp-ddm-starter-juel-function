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

import com.epam.digital.data.platform.dgtldcmnt.client.DigitalDocumentServiceInternalApiV2RestClient;
import com.epam.digital.data.platform.dgtldcmnt.dto.InternalApiDocumentMetadataDto;
import com.epam.digital.data.platform.dgtldcmnt.multipart.ByteArrayMultipartFile;
import com.epam.digital.data.platform.el.juel.dto.DocumentMetadata;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class with JUEL function that used for saving files with content passed via byte array in
 * digital-document service
 *
 * @see SaveDigitalDocumentJuelFunction#save_digital_document(byte[], String) The function itself
 */
@Slf4j
@Component
public class SaveDigitalDocumentJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "save_digital_document";

  protected SaveDigitalDocumentJuelFunction() {
    super(JUEL_FUNCTION_NAME, byte[].class, String.class);
  }

  /**
   * Static JUEL function that sends request for storing the file with content passed as byte array
   *
   * @param content        content of the file to store
   * @param targetFileName new file name for this file
   * @return stored file metadata
   */
  public static DocumentMetadata save_digital_document(byte[] content, String targetFileName) {
    if (Objects.isNull(content) || Objects.isNull(targetFileName)) {
      log.warn("save_digital_document wasn't executed because one of the inputs is null");
      return DocumentMetadata.builder().build();
    }

    var restClient = getBean(DigitalDocumentServiceInternalApiV2RestClient.class);
    var rootProcessInstanceId = getExecution().getRootProcessInstanceId();
    var multipartFile = toMultipartFile(content, targetFileName);
    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();
    var headers = createHeaders(accessToken);
    var metadataDto = restClient.upload(rootProcessInstanceId, targetFileName, multipartFile, headers);

    return toDocumentMetadata(metadataDto);
  }

  private static MultipartFile toMultipartFile(byte[] content, String targetFileName) {
    var tika = getBean(Tika.class);

    return ByteArrayMultipartFile.builder()
        .originalFilename(targetFileName)
        .bytes(content)
        .contentType(tika.detect(content, targetFileName))
        .build();
  }

  private static DocumentMetadata toDocumentMetadata(InternalApiDocumentMetadataDto metadataDto) {
    return DocumentMetadata.builder().id(metadataDto.getId()).name(metadataDto.getName())
        .type(metadataDto.getType()).checksum(metadataDto.getChecksum()).size(metadataDto.getSize())
        .build();
  }
}
