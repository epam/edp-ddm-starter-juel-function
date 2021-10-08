package com.epam.digital.data.platform.el.juel.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;

@RequiredArgsConstructor
public class KeycloakProvider {

  private final Keycloak systemUserKeycloak;

  public String getSystemUserAccessToken() {
    return systemUserKeycloak.tokenManager().getAccessTokenString();
  }

}
