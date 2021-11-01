package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.el.juel.dto.UserFormDataDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.spin.Spin;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that retrieves form data object
 *
 * @see SubmissionJuelFunction#submission(String) The function itself
 */
@Component
public class SubmissionJuelFunction extends AbstractSubmissionJuelFunction {

  private static final String SUBMISSION_FUNCTION_NAME = "submission";
  private static final String SUBMISSION_OBJ_VAR_NAME_FORMAT = "submission-juel-function-result-object-%s";

  public SubmissionJuelFunction() {
    super(SUBMISSION_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that retrieves form data from ceph
   * <p>
   * Checks if there already is an object with form data info in Camunda execution context and
   * returns it if it exists or else reads form data from ceph
   *
   * @param bpmnElementId event or activity identifier
   * @return form data {@link UserFormDataDto} representation
   */
  public static UserFormDataDto submission(String bpmnElementId) {
    final var execution = (ExecutionEntity) getExecution();
    final var variableAccessor = getVariableAccessor();

    var submissionResultObjectName = String
        .format(SUBMISSION_OBJ_VAR_NAME_FORMAT, bpmnElementId);
    UserFormDataDto storedObject = variableAccessor.getVariable(submissionResultObjectName);
    if (storedObject != null) {
      return storedObject;
    }

    var cephKey = getCephKey(bpmnElementId, execution);
    var formData = getFormDataFromCeph(cephKey);
    var data = formData.map(FormDataDto::getData).map(Spin::JSON).orElse(null);
    var userFormData = new UserFormDataDto(data);

    variableAccessor.removeVariable(submissionResultObjectName);
    variableAccessor.setVariableTransient(submissionResultObjectName, userFormData);
    return userFormData;
  }
}
