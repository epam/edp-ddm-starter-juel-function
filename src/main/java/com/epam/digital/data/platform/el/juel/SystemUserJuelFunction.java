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

package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.el.juel.dto.UserDto;
import com.epam.digital.data.platform.integration.idm.service.IdmService;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that resolves a system user info object
 *
 * @see SystemUserJuelFunction#system_user() The function itself
 */
@Component
public class SystemUserJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String SYSTEM_USER_FUNCTION_NAME = "system_user";
  public static final String SYSTEM_USER_OBJ_VAR_NAME = "system-user-juel-function-result-object";

  public SystemUserJuelFunction() {
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
    final var variableAccessor = getVariableAccessor();

    UserDto storedObject = variableAccessor.getVariable(SYSTEM_USER_OBJ_VAR_NAME);
    if (storedObject != null) {
      return storedObject;
    }

    var idmService = getBean("system-user-keycloak-client-service", IdmService.class);
    var accessToken = idmService.getClientAccessToken();
    var claims = parseClaims(accessToken);

    var systemUser = new UserDto(claims.getPreferredUsername(), accessToken, claims);

    variableAccessor.removeVariable(SYSTEM_USER_OBJ_VAR_NAME);
    variableAccessor.setVariableTransient(SYSTEM_USER_OBJ_VAR_NAME, systemUser);
    return systemUser;
  }
}
