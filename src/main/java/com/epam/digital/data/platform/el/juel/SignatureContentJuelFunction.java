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

import com.epam.digital.data.platform.dso.api.dto.SignFormat;
import com.epam.digital.data.platform.dso.client.DigitalSignatureRestClient;
import com.epam.digital.data.platform.el.juel.dto.SignDataDto;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that used to retrieve content from signed data
 *
 * @see SignatureContentJuelFunction#signature_content(String, SignFormat) The function itself
 */
@Component
public class SignatureContentJuelFunction extends AbstractSignatureJuelFunction {

  private static final String SIGNATURE_CONTENT_FUNCTION_NAME = "signature_content";
  private static final String SIGNATURE_UNABLE_GET_CONTENT = "SIGNATURE_UNABLE_GET_CONTENT";

  public SignatureContentJuelFunction() {
    super(SIGNATURE_CONTENT_FUNCTION_NAME, String.class, SignFormat.class);
  }

  /**
   * Receives content from signed data
   *
   * @param data      data that includes a signature in Base64 format
   * @param container received file type
   * @return {@link SignDataDto} representing the signature content.
   */
  public static SignDataDto signature_content(String data, SignFormat container) {
    var restClient = getBean(DigitalSignatureRestClient.class);
    var result = sendRequest(data, container, SIGNATURE_UNABLE_GET_CONTENT, restClient::content);
    var allContent = result.getContent();
    var firstContent = allContent.isEmpty() ? null : allContent.get(0);
    return SignDataDto.builder()
        .allContent(allContent)
        .content(firstContent)
        .build();
  }
}
