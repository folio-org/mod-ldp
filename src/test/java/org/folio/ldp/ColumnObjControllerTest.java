package org.folio.ldp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.jdbc.Sql;

import jdk.jfr.Timestamp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.ClassRule;

import java.util.List;

import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {ColumnObjControllerTest.Initializer.class})
//@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})

@AutoConfigureMockMvc
public class ColumnObjControllerTest {
  
  @ClassRule
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("integration-tests-db")
    .withUsername("sa")
    .withPassword("sa")
    .withInitScript("drop-and-create.sql");


  static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
          TestPropertyValues.of(
            "dbinfo.url=" + postgreSQLContainer.getJdbcUrl(),
            "dbinfo.user=" + postgreSQLContainer.getUsername(),
            "dbinfo.pass=" + postgreSQLContainer.getPassword()
          ).applyTo(configurableApplicationContext.getEnvironment());
      }
  }

  @Autowired
  private MockMvc mvc;

  public final static String QUERY_PATH = "/ldp/db/columns";

  @Autowired
  private ColumnObjController coController;

  
  @Test 
  public void getMVCColumns() throws Exception {
    mvc.perform(get(QUERY_PATH)
      .contentType("application/json")
      .param("schema", "public")
      .param("table", "user_users"))
        .andExpect(status().isOk());
  }
  

  @Test
  public void getColumns() throws Exception {
    List<ColumnObj> colList = coController.getColumnsForTable("public", "user_users");
    assertEquals(10,colList.size());

  }

  @Test 
  public void getColumnsAsMap() throws Exception {

  }
  
}
