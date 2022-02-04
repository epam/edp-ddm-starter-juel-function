package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.dataaccessor.sysvar.StartMessagePayloadStorageKeyVariable;
import com.epam.digital.data.platform.el.juel.dto.MessagePayloadReadOnlyDto;
import com.epam.digital.data.platform.storage.message.service.MessagePayloadStorageService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that resolves a message payload object
 *
 * @see MessagePayloadJuelFunction#message_payload(String) The function itself
 */
@Component
public class MessagePayloadJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String MESSAGE_PAYLOAD_FUNCTION_NAME = "message_payload";
  private static final String MESSAGE_PAYLOAD_OBJ_VAR_NAME_FORMAT = "message-payload-juel-function-result-object-%s";

  protected MessagePayloadJuelFunction() {
    super(MESSAGE_PAYLOAD_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that retrieves message payload from storage
   * <p>
   * Checks if there already is an object with message payload in Camunda execution context and
   * returns it if it exists or else reads message payload from storage
   *
   * @param bpmnElementId event or activity identifier
   * @return message payload {@link MessagePayloadReadOnlyDto} representation
   */
  public static MessagePayloadReadOnlyDto message_payload(String bpmnElementId) {
    final var variableAccessor = getVariableAccessor();

    final var messagePayloadResultObjectName = String
        .format(MESSAGE_PAYLOAD_OBJ_VAR_NAME_FORMAT, bpmnElementId);
    MessagePayloadReadOnlyDto storedObject = variableAccessor
        .getVariable(messagePayloadResultObjectName);
    if (Objects.nonNull(storedObject)) {
      return storedObject;
    }

    var messagePayloadDto = getMessagePayloadFromStorage(bpmnElementId)
        .orElse(MessagePayloadReadOnlyDto.builder().data(Map.of()).build());

    variableAccessor.removeVariable(messagePayloadResultObjectName);
    variableAccessor.setVariableTransient(messagePayloadResultObjectName, messagePayloadDto);
    return messagePayloadDto;
  }

  private static Optional<MessagePayloadReadOnlyDto> getMessagePayloadFromStorage(
      String bpmnElementId) {
    var startEventId = getExecution().getProcessDefinition().getInitial().getId();
    var storageService = getBean(MessagePayloadStorageService.class);
    if (bpmnElementId.equals(startEventId)) {
      return storageService.getMessagePayload(getStartMessagePayloadStorageKey())
          .map(dto -> MessagePayloadReadOnlyDto.builder().data(dto.getData()).build());
    } else {
      return Optional.empty();
    }
  }

  private static String getStartMessagePayloadStorageKey() {
    final var execution = getExecution();
    final var startMessagePayloadStorageKeyVariable = getBean(
        StartMessagePayloadStorageKeyVariable.class);
    return startMessagePayloadStorageKeyVariable.from(execution).get();
  }
}
