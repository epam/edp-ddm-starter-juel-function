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

package com.epam.digital.data.platform.el.juel.dto;

import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto that represents structure with a username(mandatory) and user token(optional)
 * <p>
 * Provides getter for data that is stored in token
 */
@RequiredArgsConstructor
public class UserDto {

  @Getter
  private final String userName;
  @Getter
  private String accessToken;
  private JwtClaimsDto jwtClaimsDto;

  /**
   * Constructor for structure with filled token
   *
   * @param userName     preferredUserName of a user
   * @param token        current user token (may be {@code null})
   * @param jwtClaimsDto parsed user token claims dto (has to be parsed from the {@code token}, will
   *                     be {@code null} if {@code token} is {@code null}
   */
  public UserDto(String userName, String token, JwtClaimsDto jwtClaimsDto) {
    this(userName);
    this.accessToken = token;
    this.jwtClaimsDto = token == null ? null : Objects.requireNonNull(jwtClaimsDto);
  }

  public String getDrfo() {
    return jwtClaimsDto == null ? null : jwtClaimsDto.getDrfo();
  }

  public String getEdrpou() {
    return jwtClaimsDto == null ? null : jwtClaimsDto.getEdrpou();
  }

  public String getFullName() {
    return jwtClaimsDto == null ? null : jwtClaimsDto.getFullName();
  }

  public List<String> getRoles() {
    return jwtClaimsDto == null ? Collections.emptyList() : jwtClaimsDto.getRoles();
  }

  public String getSubjectType() {
    return jwtClaimsDto == null || jwtClaimsDto.getSubjectType() == null ? null
        : jwtClaimsDto.getSubjectType().name();
  }

  public Boolean isRepresentative() {
    return jwtClaimsDto == null ? null : jwtClaimsDto.isRepresentative();
  }

  public Map<String, List<String>> getAttributes() {
    return jwtClaimsDto == null ? new HashMap<>() : jwtClaimsDto.getOtherClaims().entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            e -> {
              var value = e.getValue();
              if (value instanceof List) {
                return (List<String>) value;
              } else {
                return List.of((String) value);
              }
            }));
  }
}
