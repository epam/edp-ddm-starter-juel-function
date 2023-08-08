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

import com.epam.digital.data.platform.dso.api.dto.SignFormat;
import com.epam.digital.data.platform.dso.api.dto.SignInfoRequestDto;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import java.util.function.BiFunction;
import org.springframework.http.HttpHeaders;

public abstract class AbstractSignatureJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  /**
   * Inherited constructor of AbstractSignatureJuelFunction
   */
  protected AbstractSignatureJuelFunction(String juelFunctionName, Class<?>... paramTypes) {
    super(juelFunctionName, paramTypes);
  }

  protected static <T> T sendRequest(String data, SignFormat container,
      BiFunction<SignInfoRequestDto, HttpHeaders, T> dsoOperation) {
    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();
    var signInfoRequestDto = SignInfoRequestDto.builder()
        .data(data)
        .container(container)
        .build();
    var headers = createHeaders(accessToken);
    return dsoOperation.apply(signInfoRequestDto, headers);
  }
}
