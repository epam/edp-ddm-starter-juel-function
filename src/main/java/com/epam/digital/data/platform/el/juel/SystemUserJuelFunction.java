package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.el.juel.keycloak.KeycloakProvider;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that resolves a system user info object
 *
 * @see SystemUserJuelFunction#system_user() The function itself
 */
@Component
public class SystemUserJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String SYSTEM_USER_FUNCTION_NAME = "system_user";
  public static final String SYSTEM_USER_OBJ_VAR_NAME = "system_user-juel-function-result-object";

  protected SystemUserJuelFunction() {
    super(SYSTEM_USER_FUNCTION_NAME);
  }

  /**
   * Static JUEL function that resolves an {@link UserDto} object of the system user.
   * <p>
   * Checks if there already is an object with system user info in Camunda execution context and
   * returns it if it exists or else reads a system user from system realm, parses token claims and
   * creates an {@link UserDto} object with all found data
   *
   * @return system user {@link UserDto} representation
   */
  public static UserDto system_user() {
    var execution = getExecution();

    var storedObject = (UserDto) execution.getVariable(SYSTEM_USER_OBJ_VAR_NAME);
    if (storedObject != null) {
      return storedObject;
    }

    var keycloakProvider = getBean(KeycloakProvider.class);
    var accessToken = keycloakProvider.getSystemUserAccessToken();
    var claims = parseClaims(accessToken);

    var systemUser = new UserDto(claims.getPreferredUsername(), accessToken, claims);

    execution.removeVariable(SYSTEM_USER_OBJ_VAR_NAME);
    execution.setVariableLocalTransient(SYSTEM_USER_OBJ_VAR_NAME, systemUser);
    return systemUser;
  }
}
