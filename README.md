# ddm-starter-juel-function

### Overview

* Project with configuration for camunda JUEL functions.

### Usage

1. Specify dependency in your service:

```xml
<dependencies>
    ...
    <dependency>
          <groupId>com.epam.digital.data.platform</groupId>
          <artifactId>ddm-starter-juel-function</artifactId>
          <version>...</version>
        </dependency>
    ...
</dependencies>
```

2. Auto-configuration should be activated through the `@SpringBootApplication` annotation or
   using `@EnableAutoConfiguration` annotation in main class;

3. Define these properties:

```yaml
keycloak:
  url: base keycloak url
  system-user:
    realm: system user realm name
    client-id: system user client identifier
    client-secret: system user client secret
```

### Available JUEL functions

* `initiator` - gets information of the business-process initiator;
* `completer` - gets information of the user-task completer;
* `system_user` - gets information of the system user;
* `submission` - gets object with form data information;
* `sign_submission` - gets object with form data and signature;
* `get_variable` - gets access to a context variables;
* `set_variable` - sets new value to an execution context;
* `set_transient_variable` - sets new transient value to an execution context.

### Test execution

* Tests could be run via maven command:
   * `mvn verify` OR using appropriate functions of your IDE.

### License

The ddm-starter-juel-function is Open Source software released under
the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).