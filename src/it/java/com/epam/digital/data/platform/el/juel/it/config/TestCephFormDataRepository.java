package com.epam.digital.data.platform.el.juel.it.config;

import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.repository.FormDataRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCephFormDataRepository implements FormDataRepository {

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
  public void putFormData(String key, FormDataDto formDataDto) {
    storage.put(key, formDataDto);
  }

  @Override
  public Set<String> getKeys(String prefix) {
    return storage.keySet().stream().filter(k -> k.startsWith(prefix)).collect(Collectors.toSet());
  }

  @Override
  public void delete(Set<String> keys) {
    keys.forEach(storage::remove);
  }
}
