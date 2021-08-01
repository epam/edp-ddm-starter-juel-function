package com.epam.digital.data.platform.el.juel.it;

import com.epam.digital.data.platform.el.juel.ceph.CephKeyProvider;
import com.epam.digital.data.platform.el.juel.it.config.TestFormDataCephServiceImpl;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.util.Objects;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestCamundaApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {

  protected static final String TOKEN_USER_NAME = "testuser";
  protected static final String TOKEN_FULL_NAME = "test user user";

  @Autowired
  private RuntimeService runtimeService;
  @Autowired
  private TaskService taskService;
  @Autowired
  private TestFormDataCephServiceImpl formDataCephService;
  @Autowired
  private CephKeyProvider cephKeyProvider;

  private String accessToken;

  @Before
  public void setAccessToken() throws IOException {
    accessToken = new String(ByteStreams.toByteArray(Objects
        .requireNonNull(BaseIT.class.getResourceAsStream("/testuserAccessToken.txt"))));
  }

  protected RuntimeService runtimeService() {
    return runtimeService;
  }

  protected String accessToken() {
    return accessToken;
  }

  protected TaskService taskService() {
    return taskService;
  }

  protected TestFormDataCephServiceImpl formDataCephService() {
    return formDataCephService;
  }

  protected CephKeyProvider cephKeyProvider() {
    return cephKeyProvider;
  }
}
