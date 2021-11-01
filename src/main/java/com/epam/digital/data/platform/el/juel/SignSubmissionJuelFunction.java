package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.el.juel.dto.SignUserFormDataDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.spin.Spin;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that retrieves form data object
 *
 * @see SignSubmissionJuelFunction#sign_submission(String) The function itself
 */
@Component
public class SignSubmissionJuelFunction extends AbstractSubmissionJuelFunction {

  private static final String SIGN_SUBMISSION_FUNCTION_NAME = "sign_submission";
  private static final String SIGN_SUBMISSION_OBJ_VAR_NAME_FORMAT = "sign_submission-juel-function-result-object-%s";

  public SignSubmissionJuelFunction() {
    super(SIGN_SUBMISSION_FUNCTION_NAME, String.class);
  }

  /**
   * Static JUEL function that retrieves form data and signature from ceph
   * <p>
   * Checks if there already is an object with form data and signature in Camunda execution context
   * and returns it if it exists or else reads data from ceph
   *
   * @param bpmnElementId event or activity identifier
   * @return form data and signature {@link SignUserFormDataDto} representation
   */
  public static SignUserFormDataDto sign_submission(String bpmnElementId) {
    final var execution = (ExecutionEntity) getExecution();
    final var variableAccessor = getVariableAccessor();

    var signSubmissionResultObjectName = String
        .format(SIGN_SUBMISSION_OBJ_VAR_NAME_FORMAT, bpmnElementId);
    SignUserFormDataDto storedObject = variableAccessor.getVariable(signSubmissionResultObjectName);
    if (storedObject != null) {
      return storedObject;
    }

    var cephKey = getCephKey(bpmnElementId, execution);
    var formData = getFormDataFromCeph(cephKey);

    var data = formData.map(FormDataDto::getData).map(Spin::JSON).orElse(null);
    var signature = formData.map(FormDataDto::getSignature).orElse(null);
    var signUserFormDataDto = new SignUserFormDataDto(data, signature,
        formData.isEmpty() ? null : cephKey);

    variableAccessor.removeVariable(signSubmissionResultObjectName);
    variableAccessor.setVariableTransient(signSubmissionResultObjectName, signUserFormDataDto);
    return signUserFormDataDto;
  }
}
