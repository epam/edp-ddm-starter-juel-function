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

import lombok.Getter;
import org.camunda.spin.json.SpinJsonNode;

/**
 * Dto that extend {@link UserFormDataDto} entity with signature and signature document id fields
 * retrieved from ceph
 */
@Getter
public class SignUserFormDataDto extends UserFormDataDto {

  private final String signature;
  private final String signatureDocumentId;

  public SignUserFormDataDto(SpinJsonNode formData, String signature, String signatureDocumentId) {
    super(formData);
    this.signature = signature;
    this.signatureDocumentId = signatureDocumentId;
  }
}
