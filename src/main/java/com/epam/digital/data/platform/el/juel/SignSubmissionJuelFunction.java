package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.el.juel.dto.SignUserFormDataDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import java.util.Optional;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.spin.Spin;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that retrieves form data object
 *
 * @see SignSubmissionJuelFunction#sign_submission(String) The function itself
 */
@Component
public class SignSubmissionJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String SIGN_SUBMISSION_FUNCTION_NAME = "sign_submission";
  private static final String SIGN_SUBMISSION_OBJ_VAR_NAME_FORMAT = "sign_submission-juel-function-result-object-%s";
  private static final String START_FORM_CEPH_KEY = "start_form_ceph_key";

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

    var signSubmissionResultObjectName = String
        .format(SIGN_SUBMISSION_OBJ_VAR_NAME_FORMAT, bpmnElementId);

    var storedObject = (SignUserFormDataDto) execution.getVariable(signSubmissionResultObjectName);

    if (storedObject != null) {
      return storedObject;
    }

    var startEventId = execution.getProcessDefinition().getInitial().getId();
    var cephKey = getCephKey(bpmnElementId, startEventId, execution);
    var formData = getFormDataFromCeph(cephKey);

    var data = formData.map(FormDataDto::getData).map(Spin::JSON).orElse(null);
    var signature = formData.map(FormDataDto::getSignature).orElse(null);
    var signUserFormDataDto = new SignUserFormDataDto(data, signature,
        formData.isEmpty() ? null : cephKey);
    execution.removeVariable(signSubmissionResultObjectName);
    execution.setVariableLocalTransient(signSubmissionResultObjectName, signUserFormDataDto);
    return signUserFormDataDto;
  }

  private static String getCephKey(String bpmnElementId, String startEventId, ExecutionEntity execution){
    String cephKey;
    if (bpmnElementId.equals(startEventId)) {
      cephKey = (String) execution.getVariable(START_FORM_CEPH_KEY);
    } else {
      var cephKeyProvider = getBean(CephKeyProvider.class);
      cephKey = cephKeyProvider.generateKey(bpmnElementId, execution.getProcessInstanceId());
    }
    return cephKey;
  }

  private static Optional<FormDataDto> getFormDataFromCeph(String cephKey) {
    var cephService = getBean(FormDataCephService.class);
    return cephService.getFormData(cephKey);
  }
}
