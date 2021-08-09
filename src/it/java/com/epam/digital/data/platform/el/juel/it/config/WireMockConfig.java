package com.epam.digital.data.platform.el.juel.it.config;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WireMockConfig {

  @Bean(destroyMethod = "stop")
  @Qualifier("keycloakMockServer")
  public WireMockServer keycloakMockServer(@Value("${keycloak.url}") String urlStr)
      throws MalformedURLException {
    URL url = new URL(urlStr);
    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(url.getPort()));
    WireMock.configureFor(url.getHost(), url.getPort());
    wireMockServer.start();
    return wireMockServer;
  }
}
