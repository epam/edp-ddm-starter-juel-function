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

import com.epam.digital.data.platform.dgtldcmnt.client.DigitalDocumentServiceInternalApiRestClient;
import com.epam.digital.data.platform.dgtldcmnt.dto.InternalApiDocumentMetadataDto;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.util.List;
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
class GetDigitalDocumentMetadataJuelFunctionTest {

  static final String ROOT_PROCESS_INSTANCE_ID = "processInstanceId";

  @Mock
  DigitalDocumentServiceInternalApiRestClient client;
  @Mock
  ExecutionEntity executionEntity;
  @Mock
  IdmService idmService;
  @Mock
  ApplicationContext applicationContext;

  @InjectMocks
  GetDigitalDocumentMetadataJuelFunction juelFunction;

  @Captor
  ArgumentCaptor<HttpHeaders> httpHeadersArgumentCaptor;

  @BeforeEach
  void beforeEach() {
    Context.setExecutionContext(executionEntity);
    juelFunction.setApplicationContext(applicationContext);
    Mockito.lenient().doReturn(client)
        .when(applicationContext).getBean(DigitalDocumentServiceInternalApiRestClient.class);
    Mockito.lenient().doReturn(idmService)
        .when(applicationContext).getBean("system-user-keycloak-client-service", IdmService.class);
    Mockito.lenient().doReturn(ROOT_PROCESS_INSTANCE_ID).when(executionEntity)
        .getRootProcessInstanceId();
  }

  @Test
  void shouldReturnDigitalDocumentMetadata() {
    var targetFileName = "file.txt";
    var contentType = "application/json";
    var documentId = "documentId";

    Mockito.doReturn("accessToken").when(idmService).getClientAccessToken();

    var metadataDto = InternalApiDocumentMetadataDto.builder()
        .id(documentId)
        .checksum("someCheckSum")
        .size(1)
        .type(contentType)
        .name(targetFileName)
        .build();
    Mockito.doReturn(metadataDto).when(client)
        .getMetadata(eq(ROOT_PROCESS_INSTANCE_ID), eq(documentId), any());

    final var actualDocumentDto = GetDigitalDocumentMetadataJuelFunction.get_digital_document_metadata(
        documentId);

    Assertions.assertThat(actualDocumentDto)
        .hasFieldOrPropertyWithValue("id", "documentId")
        .hasFieldOrPropertyWithValue("name", targetFileName)
        .hasFieldOrPropertyWithValue("type", contentType)
        .hasFieldOrPropertyWithValue("checksum", "someCheckSum")
        .hasFieldOrPropertyWithValue("size", 1L);

    Mockito.verify(client)
        .getMetadata(eq(ROOT_PROCESS_INSTANCE_ID), eq(documentId),
            httpHeadersArgumentCaptor.capture());
    var actualHeaders = httpHeadersArgumentCaptor.getValue();
    Assertions.assertThat(actualHeaders)
        .containsEntry("X-Access-Token", List.of("accessToken"));
  }
}
