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

import com.epam.digital.data.platform.starter.errorhandling.dto.SystemErrorDto;
import com.epam.digital.data.platform.starter.trembita.integration.base.config.AuthorizationProperties;
import com.epam.digital.data.platform.starter.trembita.integration.base.config.RegistryProperties;
import com.epam.digital.data.platform.starter.trembita.integration.base.config.TrembitaExchangeGatewayProperties;
import com.epam.digital.data.platform.starter.trembita.integration.base.exception.TrembitaConfigurationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class with JUEL function that returns auth token for Trembita integration
 *
 * @see GetTrembitaAuthTokenJuelFunction#get_trembita_auth_token(String) The
 * function itself
 */
@Component
public class GetTrembitaAuthTokenJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  private static final String JUEL_FUNCTION_NAME = "get_trembita_auth_token";

  protected GetTrembitaAuthTokenJuelFunction() {
    super(JUEL_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that returns auth token for trembita integration with third-party registry
   *
   * @param registryName name of integration
   */
  public static String get_trembita_auth_token(String registryName) {
    var trembitaConnectionProperties =
        getBean("trembitaExchangeGatewayProperties", TrembitaExchangeGatewayProperties.class);
    return Optional.ofNullable(trembitaConnectionProperties.getRegistries().get(registryName))
        .map(RegistryProperties::getAuth)
        .map(AuthorizationProperties::getSecret)
        .map(AuthorizationProperties.Secret::getToken)
        .orElseThrow(
            () ->
                new TrembitaConfigurationException(
                    SystemErrorDto.builder()
                        .code("CLIENT_ERROR")
                        .message(
                            String.format(
                                "No authorization token for registry %s configured", registryName))
                        .build()));
  }
}
