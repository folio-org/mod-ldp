package org.folio.ldp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.jdbc.Sql;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.junit.ClassRule;

import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {SchemaTableTest.Initializer.class})
//@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})
@AutoConfigureMockMvc
public class SchemaTableTest {
  @ClassRule
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("schema-table-integration-tests-db")
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

  @Autowired private MockMvc mvc;
  @Autowired private ApplicationContext context;

  @Autowired DBInfoService dbInfoService;

  public final static String QUERY_PATH = "/ldp/db/tables";

  @Test
  public void getTables() throws Exception {
    Map<String, String> dbMap = dbInfoService.getDBInfo();
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"),
     dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");
    CrawlTableObjRepository ctor = new CrawlTableObjRepository();
    ctor.setConnection(dmds.getConnection());
    List<TableObj> tableList = ctor.getAllTablesBySchema();
    for(TableObj t : tableList) {
      System.out.print(t.getTableName() + ", " + t.getTableSchema());
    }   
  }  

  @Test
  public void getMVCTables() throws Exception {
    mvc.perform(get(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku"))
        .andExpect(status().isOk());
  }
}


