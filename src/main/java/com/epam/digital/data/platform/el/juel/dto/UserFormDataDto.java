package com.epam.digital.data.platform.el.juel.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.camunda.spin.json.SpinJsonNode;

/**
 * Dto that represents structure with a user form data that is retrieved from ceph
 */
@Getter
@RequiredArgsConstructor
public class UserFormDataDto {

  private final SpinJsonNode formData;
}

