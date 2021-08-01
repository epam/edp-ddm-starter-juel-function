package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.el.juel.dto.UserFormDataDto;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import java.util.Optional;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.spin.Spin;
import org.springframework.stereotype.Component;

/**
 * Class with JUEL function that retrieves form data object
 *
 * @see SubmissionJuelFunction#submission(String) The function itself
 */
@Component
public class SubmissionJuelFunction extends AbstractApplicationContextAwareJuelFunction {

  private static final String SUBMISSION_FUNCTION_NAME = "submission";
  private static final String SUBMISSION_OBJ_VAR_NAME_FORMAT = "submission-juel-function-result-object-%s";
  private static final String START_FORM_CEPH_KEY = "start_form_ceph_key";

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

    var submissionResultObjectName = String
        .format(SUBMISSION_OBJ_VAR_NAME_FORMAT, bpmnElementId);

    var storedObject = (UserFormDataDto) execution.getVariable(submissionResultObjectName);

    if (storedObject != null) {
      return storedObject;
    }

    var startEventId = execution.getProcessDefinition().getInitial().getId();
    String cephKey;

    if (bpmnElementId.equals(startEventId)) {
      cephKey = (String) execution.getVariable(START_FORM_CEPH_KEY);
    } else {
      var cephKeyProvider = getBean(CephKeyProvider.class);
      cephKey = cephKeyProvider.generateKey(bpmnElementId, execution.getProcessInstanceId());
    }
    var formData = getFormDataFromCeph(cephKey);
    var data = formData.map(FormDataDto::getData).map(Spin::JSON).orElse(null);
    var userFormData = new UserFormDataDto(data);
    execution.removeVariable(submissionResultObjectName);
    execution.setVariableLocalTransient(submissionResultObjectName, userFormData);
    return userFormData;
  }

  private static Optional<FormDataDto> getFormDataFromCeph(String cephKey) {
    var cephService = getBean(FormDataCephService.class);
    return cephService.getFormData(cephKey);
  }
}
