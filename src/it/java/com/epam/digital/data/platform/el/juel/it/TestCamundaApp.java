package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.el.juel.config.JuelConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JuelConfig.class)
@EnableAutoConfiguration
public class TestCamundaApp {

  public static void main(String[] args) {
    SpringApplication.run(TestCamundaApp.class, args);
  }
}
