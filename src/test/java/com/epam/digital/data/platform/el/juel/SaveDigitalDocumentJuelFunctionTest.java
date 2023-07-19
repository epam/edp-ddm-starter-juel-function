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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;

import com.epam.digital.data.platform.dgtldcmnt.client.DigitalDocumentServiceInternalApiV2RestClient;
import com.epam.digital.data.platform.dgtldcmnt.dto.InternalApiDocumentMetadataDto;
import com.epam.digital.data.platform.dgtldcmnt.multipart.ByteArrayMultipartFile;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.util.List;
import org.apache.tika.Tika;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
public class SaveDigitalDocumentJuelFunctionTest {

  static final String ROOT_PROCESS_INSTANCE_ID = "processInstanceId";

  @Mock
  DigitalDocumentServiceInternalApiV2RestClient client;
  @Mock
  ExecutionEntity executionEntity;
  @Mock
  Tika tika;
  @Mock
  IdmService idmService;
  @Mock
  ApplicationContext applicationContext;

  @InjectMocks
  SaveDigitalDocumentJuelFunction juelFunction;

  @Captor
  ArgumentCaptor<HttpHeaders> httpHeadersArgumentCaptor;

  @BeforeEach
  void beforeEach() {
    Context.setExecutionContext(executionEntity);
    juelFunction.setApplicationContext(applicationContext);
    Mockito.lenient().doReturn(client)
        .when(applicationContext).getBean(DigitalDocumentServiceInternalApiV2RestClient.class);
    Mockito.lenient().doReturn(idmService)
        .when(applicationContext).getBean("system-user-keycloak-client-service", IdmService.class);
    Mockito.lenient().doReturn(tika).when(applicationContext).getBean(Tika.class);
    Mockito.lenient().doReturn(ROOT_PROCESS_INSTANCE_ID).when(executionEntity)
        .getRootProcessInstanceId();
  }

  @Test
  void shouldDoNothingIfContentIsNull() {
    final var actualDocumentDto = SaveDigitalDocumentJuelFunction.save_digital_document(null,
        "file");

    Assertions.assertThat(actualDocumentDto)
        .hasFieldOrPropertyWithValue("id", null)
        .hasFieldOrPropertyWithValue("name", null)
        .hasFieldOrPropertyWithValue("type", null)
        .hasFieldOrPropertyWithValue("checksum", null)
        .hasFieldOrPropertyWithValue("size", 0L);

    Mockito.verifyNoInteractions(client, tika);
  }

  @Test
  void shouldDoNothingIfTargetFileNameIsNull() {
    final var actualDocumentDto = SaveDigitalDocumentJuelFunction.save_digital_document(new byte[1],
        null);

    Assertions.assertThat(actualDocumentDto)
        .hasFieldOrPropertyWithValue("id", null)
        .hasFieldOrPropertyWithValue("name", null)
        .hasFieldOrPropertyWithValue("type", null)
        .hasFieldOrPropertyWithValue("checksum", null)
        .hasFieldOrPropertyWithValue("size", 0L);

    Mockito.verifyNoInteractions(client, tika);
  }

  @Test
  void shouldSendRequestToDigitalDocumentService() {
    var content = new byte[]{1, 2, 3};
    var targetFileName = "file.txt";
    var contentType = "application/json";

    Mockito.doReturn("accessToken").when(idmService).getClientAccessToken();
    Mockito.doReturn("application/json").when(tika).detect(content, targetFileName);

    var expectedMultipartFile = ByteArrayMultipartFile.builder()
        .bytes(content)
        .originalFilename(targetFileName)
        .contentType(contentType)
        .build();
    var metadataDto = InternalApiDocumentMetadataDto.builder()
        .id("documentId")
        .checksum("someCheckSum")
        .size(content.length)
        .type(contentType)
        .name(targetFileName)
        .build();
    Mockito.doReturn(metadataDto).when(client)
        .upload(eq(ROOT_PROCESS_INSTANCE_ID), eq(targetFileName), refEq(expectedMultipartFile),
            any());

    final var actualDocumentDto = SaveDigitalDocumentJuelFunction.save_digital_document(content,
        targetFileName);

    Assertions.assertThat(actualDocumentDto)
        .hasFieldOrPropertyWithValue("id", "documentId")
        .hasFieldOrPropertyWithValue("name", targetFileName)
        .hasFieldOrPropertyWithValue("type", contentType)
        .hasFieldOrPropertyWithValue("checksum", "someCheckSum")
        .hasFieldOrPropertyWithValue("size", (long) content.length);

    Mockito.verify(tika).detect(content, targetFileName);
    Mockito.verify(client)
        .upload(eq(ROOT_PROCESS_INSTANCE_ID), eq(targetFileName), refEq(expectedMultipartFile),
            httpHeadersArgumentCaptor.capture());
    var actualHeaders = httpHeadersArgumentCaptor.getValue();
    Assertions.assertThat(actualHeaders)
        .containsEntry("X-Access-Token", List.of("accessToken"));
  }
}
