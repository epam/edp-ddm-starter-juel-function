package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.dataaccessor.sysvar.StartFormCephKeyVariable;
import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import java.util.Optional;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

public abstract class AbstractSubmissionJuelFunction extends
    AbstractApplicationContextAwareJuelFunction {

  /**
   * Inherited constructor of AbstractSubmissionJuelFunction
   */
  protected AbstractSubmissionJuelFunction(String juelFunctionName, Class<?>... paramTypes) {
    super(juelFunctionName, paramTypes);
  }

  protected static String getCephKey(String bpmnElementId, ExecutionEntity execution) {
    var startEventId = execution.getProcessDefinition().getInitial().getId();
    String cephKey;
    if (bpmnElementId.equals(startEventId)) {
      cephKey = getStartFormCephKey();
    } else {
      var cephKeyProvider = getBean(CephKeyProvider.class);
      cephKey = cephKeyProvider.generateKey(bpmnElementId, execution.getProcessInstanceId());
    }
    return cephKey;
  }

  protected static Optional<FormDataDto> getFormDataFromCeph(String cephKey) {
    var cephService = getBean(FormDataCephService.class);
    return cephService.getFormData(cephKey);
  }

  private static String getStartFormCephKey() {
    final var execution = (ExecutionEntity) getExecution();
    final var startFormCephKeyVariable = getBean(StartFormCephKeyVariable.class);
    return startFormCephKeyVariable.from(execution).get();
  }
}
