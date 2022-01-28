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

package com.epam.digital.data.platform.el.juel.config;

import com.epam.digital.data.platform.el.juel.AbstractApplicationContextAwareJuelFunction;
import com.epam.digital.data.platform.el.juel.keycloak.KeycloakProvider;
import com.epam.digital.data.platform.integration.idm.client.KeycloakAdminClient;
import com.epam.digital.data.platform.integration.idm.dto.KeycloakClientProperties;
import com.epam.digital.data.platform.integration.idm.factory.IdmClientFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The class represents a holder for beans of the camunda configuration. Each method produces a bean
 * and must be annotated with @Bean annotation to be managed by the Spring container. The method
 * should create, set up and return an instance of a bean.
 */
@Configuration
@ComponentScan(basePackageClasses = AbstractApplicationContextAwareJuelFunction.class)
public class JuelConfig {

  @Bean
  @ConditionalOnProperty(prefix = "keycloak", name = {"url", "system-user.realm"})
  @ConfigurationProperties(prefix = "keycloak.system-user")
  public KeycloakClientProperties systemUserKeycloakClientProperties() {
    return new KeycloakClientProperties();
  }

  @Bean("system-user-keycloak-admin-client")
  @ConditionalOnBean(name = "systemUserKeycloakClientProperties")
  public KeycloakAdminClient systemUserKeycloakAdminClient(@Value("${keycloak.url}") String url,
      KeycloakClientProperties systemUserKeycloakClientProperties) {
    return new IdmClientFactory().keycloakAdminClient(url, systemUserKeycloakClientProperties);
  }

  @Bean
  @ConditionalOnBean(name = "system-user-keycloak-admin-client")
  public KeycloakProvider keycloakProvider(
      @Qualifier("system-user-keycloak-admin-client") KeycloakAdminClient systemUserKeycloakAdminClient) {
    return new KeycloakProvider(systemUserKeycloakAdminClient);
  }
}
