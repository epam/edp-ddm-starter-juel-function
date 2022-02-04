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

package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import com.epam.digital.data.platform.storage.message.service.MessagePayloadStorageService;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.util.Objects;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestCamundaApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {

  protected static final String TOKEN_USER_NAME = "testuser";

  @Autowired
  private RuntimeService runtimeService;
  @Autowired
  private TaskService taskService;
  @Autowired
  private FormDataStorageService formDataStorageService;
  @Autowired
  private MessagePayloadStorageService messagePayloadStorageService;

  private String accessToken;

  @BeforeEach
  void setAccessToken() throws IOException {
    accessToken = getContentFromFile("/testuserAccessToken.txt");
  }

  protected RuntimeService runtimeService() {
    return runtimeService;
  }

  protected String accessToken() {
    return accessToken;
  }

  protected TaskService taskService() {
    return taskService;
  }

  protected FormDataStorageService formDataStorageService() {
    return formDataStorageService;
  }

  protected MessagePayloadStorageService messagePayloadStorageService() {
    return messagePayloadStorageService;
  }

  protected String getContentFromFile(String filePath) throws IOException {
    return new String(ByteStreams.toByteArray(Objects
        .requireNonNull(BaseIT.class.getResourceAsStream(filePath))));
  }
}
