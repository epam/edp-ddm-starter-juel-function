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

