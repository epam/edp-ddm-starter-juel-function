package com.epam.digital.data.platform.el.juel.config;

import com.epam.digital.data.platform.el.juel.AbstractApplicationContextAwareJuelFunction;
import com.epam.digital.data.platform.el.juel.keycloak.KeycloakProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
  public Keycloak systemUserKeycloak(
      @Value("${keycloak.url}") String serverUrl,
      @Value("${keycloak.system-user.realm}") String realm,
      @Value("${keycloak.system-user.client-id}") String clientId,
      @Value("${keycloak.system-user.client-secret}") String clientSecret) {
    return KeycloakBuilder.builder()
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .clientSecret(clientSecret)
        .serverUrl(serverUrl)
        .clientId(clientId)
        .realm(realm)
        .build();
  }

  @Bean
  @ConditionalOnBean(Keycloak.class)
  public KeycloakProvider keycloakProvider(Keycloak systemUserKeycloak) {
    return new KeycloakProvider(systemUserKeycloak);
  }
}
