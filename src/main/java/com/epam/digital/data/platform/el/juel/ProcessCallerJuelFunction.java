package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.dataaccessor.sysvar.CallerProcessInstanceIdVariable;
import com.epam.digital.data.platform.el.juel.dto.ProcessCallerDto;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that resolves a process caller object
 *
 * @see ProcessCallerJuelFunction#process_caller() The function itself
 */
@Component
public class ProcessCallerJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String PROCESS_CALLER_FUNCTION_NAME = "process_caller";
  private static final String PROCESS_CALLER_OBJ_VAR_NAME = "process-caller-juel-function-result-object";

  protected ProcessCallerJuelFunction() {
    super(PROCESS_CALLER_FUNCTION_NAME);
  }

  /**
   * Static JUEL function that retrieves process caller id from context and returns an object with
   * caller process instance info
   * <p>
   * Checks if there already is an object with caller process in Camunda execution context and
   * returns it if it exists or else reads caller process id from special variable in context
   *
   * @return process caller {@link ProcessCallerDto} representation
   */
  public static ProcessCallerDto process_caller() {
    final var variableAccessor = getVariableAccessor();

    ProcessCallerDto storedObject = variableAccessor.getVariable(PROCESS_CALLER_OBJ_VAR_NAME);
    if (Objects.nonNull(storedObject)) {
      return storedObject;
    }

    var processCallerId = getProcessCallerId();
    var processCallerDto = ProcessCallerDto.builder().id(processCallerId).build();

    variableAccessor.removeVariable(PROCESS_CALLER_OBJ_VAR_NAME);
    variableAccessor.setVariableTransient(PROCESS_CALLER_OBJ_VAR_NAME, processCallerDto);
    return processCallerDto;
  }

  private static String getProcessCallerId() {
    final var execution = getExecution();
    final var callerProcessInstanceIdVariable = getBean(
        CallerProcessInstanceIdVariable.class);
    return callerProcessInstanceIdVariable.from(execution).get();
  }
}
