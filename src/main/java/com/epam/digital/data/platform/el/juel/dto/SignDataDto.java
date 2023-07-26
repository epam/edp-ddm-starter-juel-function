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

package com.epam.digital.data.platform.el.juel.dto;

import com.epam.digital.data.platform.dso.api.dto.ContentDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object that represents content data from signature.
 */
@Getter
@Builder
public class SignDataDto {

  private final List<ContentDto> allContent;
  private final ContentDto content;
}
