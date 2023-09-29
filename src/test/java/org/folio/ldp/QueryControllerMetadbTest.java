package org.folio.ldp;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {QueryControllerMetadbTest.Initializer.class})
//@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})
@AutoConfigureMockMvc
public class QueryControllerMetadbTest
{
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
      .withInitScript("drop-and-create-metadb.sql");

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

  @Autowired
  private MockMvc mvc;

  @Autowired
  private QueryController queryController;

  @Autowired
  private ConfigObjRepository configObjRepo;

  @Autowired
  private DBInfoService dbInfoService;

  public final static String QUERY_PATH = "/ldp/db/query";

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
  public void doNothingTest() throws Exception {
    assert(true);
  }

  @Test
  public void testGetTables() throws Exception {
    Map<String, String> dbMap = dbInfoService.getDBInfo("diku");
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");

    JdbcTemplate jdbc = new JdbcTemplate(dmds);

    assertFalse(SchemaUtil.isLDP(jdbc));

    CrawlTableObjRepository tableRepository = new CrawlTableObjRepository();
    tableRepository.setJdbcTemplate(jdbc);

    String[] tables = { "users", "users__t" };
    Map<String, List<String>> tableMap = new HashMap<>();
    tableMap.put("folio_users.users", Arrays.asList(
        new String[] {"__id","__start","__end","__current","__origin","id","jsonb","creation_date","created_by","patrongroup"}));
    tableMap.put( "folio_users.users__t",
        Arrays.asList(
            new String[] {"__id","__start","__end","__current","__origin","id","updated_date","username","created_date","active","patron_group","barcode","expiration_date","enrollment_date","external_system_id","type"}));

    List<TableObj> tableList = tableRepository.findAll(true);

    assertEquals(2, tableList.size());

    for (Map.Entry<String, List<String>> tableEntry : tableMap.entrySet()) {
      boolean tableFound = false;
      for (TableObj tableObj : tableList) {
        if(tableEntry.getKey().equals(tableObj.getTableSchema() + "." + tableObj.tableName)) {
          tableFound = true;
          break;
        }
      }
      assertTrue(tableFound);
      String[] splits = tableEntry.getKey().split("\\.");
      List<ColumnObj> colList = SchemaUtil.getColumnsByTableName(jdbc, splits[0], splits[1]);
      for( String column : tableEntry.getValue()) {
        boolean columnFound = false;
        for (ColumnObj cObj : colList) {
          if(cObj.getColumnName().equals(column)) {
            columnFound = true;
            break;
          }
        }
        assertTrue(columnFound);
      }
    }
  }

  @Test
  public void queryAllUsers() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "folio_users",
            "tableName": "users",
            "columnFilters": [
              {}
            ],
            "showColumns": [],
            "orderBy": [],
            "limit": 101
          }
        ]
      }
    */

    String jsonString =
        "{\"tables\":[{\"schema\":\"folio_users\",\"tableName\":\"users\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
            .contentType("application/json")
            .header("X-Okapi-Tenant", "diku")
            .content(jsonString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));

  }

}
