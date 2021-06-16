/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.el.juel.scripting.groovy;

import java.util.Objects;
import org.camunda.bpm.engine.impl.scripting.env.ScriptEnvResolver;
import org.camunda.commons.utils.IoUtil;
import org.springframework.stereotype.Component;

/**
 * Resolver for static java functions for groovy scripts
 *
 * @see com.epam.digital.data.platform.el.juel.scripting.StaticJavaFunctionsProcessEnginePlugin
 * StaticJavaFunctionsProcessEnginePlugin
 * @see "/groovy/importStaticJavaFunctions.groovy"
 */
@Component
public class StaticJavaFunctionsGroovyScriptEnvResolver implements ScriptEnvResolver {

  private static final String GROOVY = "groovy";
  private static final String SCRIPT_ENV_PATH = "/groovy/importStaticJavaFunctions.groovy";

  @Override
  public String[] resolve(String language) {
    if (!GROOVY.equalsIgnoreCase(language)) {
      return null;
    }

    var envResource = Objects.requireNonNull(getClass().getResourceAsStream(SCRIPT_ENV_PATH),
        String.format("No script env %s found for language %s", SCRIPT_ENV_PATH, GROOVY));
    try {
      return new String[]{IoUtil.inputStreamAsString(envResource)};
    } finally {
      IoUtil.closeSilently(envResource);
    }
  }
}
