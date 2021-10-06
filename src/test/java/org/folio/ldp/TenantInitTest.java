package org.folio.ldp;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(initializers = {TenantInitTest.Initializer.class})
@AutoConfigureMockMvc
public class TenantInitTest {
  
  @Autowired private MockMvc mvc;

  //@Autowired private TenantInitController tenantInitController;

  public final static String QUERY_PATH = "/_tenant";

  @Test
  public void doInit() throws Exception {
    String jsonString = "{}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(jsonString))
        .andExpect(status().is(201));
  }
}
