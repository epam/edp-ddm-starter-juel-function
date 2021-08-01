package com.epam.digital.data.platform.el.juel.config;

import com.epam.digital.data.platform.el.juel.AbstractApplicationContextAwareJuelFunction;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The class represents a holder for beans of the camunda configuration. Each method produces a bean
 * and must be annotated with @Bean annotation to be managed by the Spring container. The method
 * should create, set up and return an instance of a bean.
 */
@Configuration
@ComponentScan(basePackageClasses = AbstractApplicationContextAwareJuelFunction.class)
public class JuelConfig {

}
