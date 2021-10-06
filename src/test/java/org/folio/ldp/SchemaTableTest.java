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

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

import org.junit.ClassRule;

import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Map;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {SchemaTableTest.Initializer.class})
//@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})
@AutoConfigureMockMvc
public class SchemaTableTest {
  @ClassRule
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("query-integration-tests-db")
    .withUsername("sa")
    .withPassword("sa");
  
  @ClassRule
  public static PostgreSQLContainer<?> externalPostgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("external-test-db")
    .withUsername("sa")
    .withPassword("sa")
    .withInitScript("drop-and-create.sql");

  static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
          TestPropertyValues.of(
            "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
            "spring.datasource.username=" + postgreSQLContainer.getUsername(),
            "spring.datasource.password=" + postgreSQLContainer.getPassword()
          ).applyTo(configurableApplicationContext.getEnvironment());
      }
  }

  @Autowired private MockMvc mvc;
  @Autowired private ApplicationContext context;

  @Autowired DBInfoService dbInfoService;
  @Autowired ConfigObjRepository configObjRepo;

  public final static String QUERY_PATH = "/ldp/db/tables";

  @Before
  public void testSetup() {
    ConfigObj config = new ConfigObj();
    JSONObject dbJson = new JSONObject();
    dbJson.put("url", externalPostgreSQLContainer.getJdbcUrl());
    dbJson.put("user", externalPostgreSQLContainer.getUsername());
    dbJson.put("pass", externalPostgreSQLContainer.getPassword());
    config.setTenant("diku");
    config.setKey("dbinfo");
    config.setValue(dbJson);
    
    configObjRepo.save(config);
  }

  @Test
  public void getTables() throws Exception {
    Map<String, String> dbMap = dbInfoService.getDBInfo("diku");
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"),
     dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");
    CrawlTableObjRepository ctor = new CrawlTableObjRepository();
    ctor.setConnection(dmds.getConnection());
    List<TableObj> tableList = ctor.getAllTablesBySchema();
    for(TableObj t : tableList) {
      System.out.print(t.getTableName() + ", " + t.getTableSchema());
    }
    assertTrue(tableList.size() > 0);   
  }  

  @Test
  public void getMVCTables() throws Exception {
    mvc.perform(get(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku"))
        .andExpect(status().isOk());
  }

  @Test
  public void getMVCTablesBadTenant() throws Exception {
    mvc.perform(get(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "deeeku"))
        .andExpect(status().is(500));
  }
}


