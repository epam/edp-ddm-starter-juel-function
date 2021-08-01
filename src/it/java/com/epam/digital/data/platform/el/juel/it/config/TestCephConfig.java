package com.epam.digital.data.platform.el.juel.it.config;

import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCephConfig {

  @Bean
  public FormDataCephService formDataCephService() {
    return new TestFormDataCephServiceImpl();
  }
}
