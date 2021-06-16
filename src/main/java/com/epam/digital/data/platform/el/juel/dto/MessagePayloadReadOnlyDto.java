package com.epam.digital.data.platform.el.juel.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessagePayloadReadOnlyDto {

  private final Map<String, Object> data;
}
