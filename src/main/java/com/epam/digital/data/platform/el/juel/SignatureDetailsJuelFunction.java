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
import com.epam.digital.data.platform.el.juel.dto.SignatureInfoDto;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function  that used to retrieve signer details from signed data
 *
 * @see SignatureDetailsJuelFunction#signature_details(String, SignFormat) The function itself
 */
@Component
public class SignatureDetailsJuelFunction extends AbstractSignatureJuelFunction {

  private static final String SIGNATURE_DETAILS_FUNCTION_NAME = "signature_details";

  public SignatureDetailsJuelFunction() {
    super(SIGNATURE_DETAILS_FUNCTION_NAME, String.class, SignFormat.class);
  }

  /**
   * Receives signer details from signed data
   *
   * @param data      data that includes a signature in Base64 format
   * @param container received file type
   * @return {@link SignatureInfoDto} representing the signature details
   */
  public static SignatureInfoDto signature_details(String data, SignFormat container) {
    var restClient = getBean(DigitalSignatureRestClient.class);
    var result = sendRequest(data, container, restClient::info);
    var allSignInfo = result.getInfo();
    var lastSigner = allSignInfo.isEmpty() ? null : allSignInfo.get(allSignInfo.size() - 1);
    return SignatureInfoDto.builder()
        .allSignInfo(allSignInfo)
        .signInfo(lastSigner)
        .build();
  }
}
