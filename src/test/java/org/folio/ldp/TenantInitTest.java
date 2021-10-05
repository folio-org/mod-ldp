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
@ContextConfiguration(initializers = {TenantInitTest.Initializer.class})
@AutoConfigureMockMvc
public class TenantInitTest {
  @ClassRule
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("query-integration-tests-db")
    .withUsername("sa")
    .withPassword("sa")
    .withInitScript("drop-and-create.sql");

  static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
          //"spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
          //"spring.datasource.username=" + postgreSQLContainer.getUsername(),
          //"spring.datasource.password=" + postgreSQLContainer.getPassword()
          "dbinfo.url=" + postgreSQLContainer.getJdbcUrl(),
          "dbinfo.user=" + postgreSQLContainer.getUsername(),
          "dbinfo.pass=" + postgreSQLContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }
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
        .andExpect(status().isOk());
  }
}
