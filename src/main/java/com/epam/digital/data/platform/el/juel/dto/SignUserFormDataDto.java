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
