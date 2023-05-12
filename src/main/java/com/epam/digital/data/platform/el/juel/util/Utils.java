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

package com.epam.digital.data.platform.el.juel.util;

import com.epam.digital.data.platform.bpms.api.dto.enums.PlatformHttpHeader;
import java.util.UUID;
import org.springframework.http.HttpHeaders;

public class Utils {

  private Utils() {
  }

  /**
   * Creates HttpHeaders with the specified access token and XSRF token.
   * Adds the XSRF token to the "Cookie" header and sets the X_ACCESS_TOKEN and X_XSRF_TOKEN headers.
   *
   * @param accessToken the access token to add to the X_ACCESS_TOKEN header
   * @return HttpHeaders with the access token and XSRF token headers added
   */
  public static HttpHeaders createHeaders(String accessToken) {
    var headers = new HttpHeaders();
    headers.add(PlatformHttpHeader.X_ACCESS_TOKEN.getName(), accessToken);
    var requestXsrfToken = UUID.randomUUID().toString();
    headers.add(PlatformHttpHeader.X_XSRF_TOKEN.getName(), requestXsrfToken);
    headers.add("Cookie", String.format("%s=%s", "XSRF-TOKEN", requestXsrfToken));
    return headers;
  }
}
