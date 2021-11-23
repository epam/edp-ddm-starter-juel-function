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

package com.epam.digital.data.platform.el.juel.it.config;

import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestFormDataCephServiceImpl implements FormDataCephService {

  private final Map<String, FormDataDto> storage = new HashMap<>();

  @Override
  public Optional<FormDataDto> getFormData(String key) {
    return Optional.ofNullable(storage.get(key));
  }

  @Override
  public void putFormData(String key, FormDataDto formDataDto) {
    storage.put(key, formDataDto);
  }
}

