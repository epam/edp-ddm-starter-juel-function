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

import com.epam.digital.data.platform.starter.trembita.integration.base.config.AuthorizationProperties;
import com.epam.digital.data.platform.starter.trembita.integration.base.config.RegistryProperties;
import com.epam.digital.data.platform.starter.trembita.integration.base.config.TrembitaExchangeGatewayProperties;
import com.epam.digital.data.platform.starter.trembita.integration.base.exception.TrembitaConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTrembitaAuthTokenJuelFunctionTest {

  @InjectMocks
  private GetTrembitaAuthTokenJuelFunction getTrembitaAuthTokenJuelFunction;
  @Mock
  private ApplicationContext applicationContext;

  @Test
  void expectValidAuthTokenFromGatewayPropertiesIfExists() {
    var trembitaGatewayProperties = new TrembitaExchangeGatewayProperties();
    var registryProperties = new RegistryProperties();
    registryProperties.setAuth(new AuthorizationProperties());
    registryProperties.getAuth().setSecret(new AuthorizationProperties.Secret());
    registryProperties.getAuth().getSecret().setToken("token");
    trembitaGatewayProperties.getRegistries().put("some-registry", registryProperties);

    getTrembitaAuthTokenJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(
            "trembitaExchangeGatewayProperties", TrembitaExchangeGatewayProperties.class))
        .thenReturn(trembitaGatewayProperties);

    var result = GetTrembitaAuthTokenJuelFunction.get_trembita_auth_token("some-registry");

    assertThat(result).isSameAs("token");
  }

  @Test
  void expectTrembitaConfigurationExceptionIfNoAuthTokenExists() {
    var trembitaGatewayProperties = new TrembitaExchangeGatewayProperties();
    var registryProperties = new RegistryProperties();
    registryProperties.setAuth(new AuthorizationProperties());
    registryProperties.getAuth().setSecret(new AuthorizationProperties.Secret());
    trembitaGatewayProperties.getRegistries().put("some-registry", registryProperties);

    getTrembitaAuthTokenJuelFunction.setApplicationContext(applicationContext);
    when(applicationContext.getBean(
            "trembitaExchangeGatewayProperties", TrembitaExchangeGatewayProperties.class))
        .thenReturn(trembitaGatewayProperties);

    var exception =
        assertThrows(
            TrembitaConfigurationException.class,
            () -> GetTrembitaAuthTokenJuelFunction.get_trembita_auth_token("some-registry"));
    assertThat(exception.getCode()).isEqualTo("CLIENT_ERROR");
    assertThat(exception.getMessage())
        .isEqualTo("No authorization token for registry some-registry configured");
  }
}
