package com.epam.digital.data.platform.el.juel.plugin;

import com.epam.digital.data.platform.el.juel.mapper.CompositeApplicationContextAwareJuelFunctionMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JuelJavaFunctionsProcessEnginePlugin extends AbstractProcessEnginePlugin {

  private final CompositeApplicationContextAwareJuelFunctionMapper compositeApplicationContextAwareJuelFunctionMapper;

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    processEngineConfiguration.getExpressionManager()
        .addFunctionMapper(compositeApplicationContextAwareJuelFunctionMapper);
  }
}
