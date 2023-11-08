package com.epam.digital.data.platform.el.juel.it.config;

import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.dto.FormDataInputWrapperDto;
import com.epam.digital.data.platform.storage.form.model.CephKeysSearchParams;
import com.epam.digital.data.platform.storage.form.repository.FormDataRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCephFormDataRepository implements FormDataRepository<CephKeysSearchParams> {

  private final Map<String, FormDataDto> storage = new HashMap<>();

  @Override
  public Optional<FormDataDto> getFormData(String key) {
    var formData = storage.get(key);
    if (Objects.isNull(formData)) {
      return Optional.empty();
    }
    return Optional.of(formData);
  }

  @Override
  public void putFormData(FormDataInputWrapperDto formDataInputWrapperDto) {
    storage.put(formDataInputWrapperDto.getKey(), formDataInputWrapperDto.getFormData());
  }

  @Override
  public Set<String> getKeysBySearchParams(CephKeysSearchParams cephKeysSearchParams) {
    return storage.keySet().stream()
        .filter(k -> k.startsWith(cephKeysSearchParams.getPrefix()))
        .collect(Collectors.toSet());
  }

  @Override
  public void delete(Set<String> keys) {
    keys.forEach(storage::remove);
  }
}
