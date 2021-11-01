package com.epam.digital.data.platform.el.juel;

import com.epam.digital.data.platform.dataaccessor.VariableAccessor;
import com.epam.digital.data.platform.dataaccessor.VariableAccessorFactory;
import com.epam.digital.data.platform.el.juel.mapper.CompositeApplicationContextAwareJuelFunctionMapper;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.Getter;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

/**
 * Base class for all custom lowcode JUEL functions. All child of the class will be added to Camunda
 * bindings list for using the function in expression language. Child must have one public static
 * method that will be mapped on JUEL function. <i>The method can be overloaded but only <b>one</b>
 * of these methods (based on parameter types list) can be used from the expression language</i>
 * <p>
 * Implements {@link ApplicationContextAware} for having access to Spring application context from
 * static methods
 *
 * @see CompositeApplicationContextAwareJuelFunctionMapper JUEL Funtion Mapper
 */
@Getter
public abstract class AbstractApplicationContextAwareJuelFunction implements
    ApplicationContextAware {

  private static ApplicationContext applicationContext;

  private final Method juelFunctionMethod;

  /**
   * Base constructor of AbstractApplicationContextAwareJuelFunction
   *
   * @param juelFunctionName name of the static method that has to be mapped on JUEL function that
   *                         will be used in expression language (must equal the real method name)
   * @param paramTypes       array of parameter types of the function
   */
  protected AbstractApplicationContextAwareJuelFunction(String juelFunctionName,
      Class<?>... paramTypes) {
    this.juelFunctionMethod = ReflectionUtils.findMethod(getClass(), juelFunctionName, paramTypes);
  }

  protected static ExecutionEntity getExecution() {
    return (ExecutionEntity) Context.getCoreExecutionContext().getExecution();
  }

  protected static <T> T getBean(@NonNull Class<T> beanType) {
    return Objects.requireNonNull(applicationContext, "Spring app context isn't initialized yet")
        .getBean(beanType);
  }

  protected static VariableAccessor getVariableAccessor() {
    return getVariableAccessor(getExecution());
  }

  protected static VariableAccessor getVariableAccessor(ExecutionEntity execution) {
    final var variableAccessorFactory = getBean(VariableAccessorFactory.class);
    return variableAccessorFactory.from(execution);
  }

  protected static JwtClaimsDto parseClaims(String accessToken) {
    final var tokenParser = getBean(TokenParser.class);
    return tokenParser.parseClaims(accessToken);
  }

  protected static ExecutionEntity getRootExecution() {
    var execution = getExecution();

    while (execution.getParent() != null) {
      execution = execution.getParent();
    }
    return execution;
  }

  @Override
  public final void setApplicationContext(@NonNull ApplicationContext applicationContext)
      throws BeansException {
    AbstractApplicationContextAwareJuelFunction.applicationContext = applicationContext;
  }
}
