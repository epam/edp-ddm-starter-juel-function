package com.epam.digital.data.platform.el.juel.it.config;

import com.epam.digital.data.platform.storage.message.dto.MessagePayloadDto;
import com.epam.digital.data.platform.storage.message.repository.MessagePayloadRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestMessagePayloadRepository implements MessagePayloadRepository {

  private final Map<String, MessagePayloadDto> storage = new HashMap<>();

  @Override
  public void putMessagePayload(String key, MessagePayloadDto messagePayloadDto) {
    storage.put(key, messagePayloadDto);
  }

  @Override
  public Optional<MessagePayloadDto> getMessagePayload(String key) {
    return Optional.ofNullable(storage.get(key));
  }

  @Override
  public void delete(String... keys) {
    for (var key : keys) {
      storage.remove(key);
    }
  }
}
