package org.folio.ldp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testcontainers.containers.PostgreSQLContainer;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {TemplateQueryControllerTest.Initializer.class})
@AutoConfigureMockMvc
public class TemplateQueryControllerTest {
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

  @Autowired
  private MockMvc mvc;

  @Autowired
  private TemplateQueryController templateQueryController;

  @Autowired
  private ConfigObjRepository configObjRepo;

  @Autowired
  private TemplateQueryService templateQueryService;

  public final static String QUERY_PATH = "/ldp/db/reports";

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
  public void testRemoteSQLRetrieval() throws Exception {
    String url = "https://raw.githubusercontent.com/folio-org/folio-analytics/de26cee264b695c59c68142707071befa7e2d003/reports/count_loans_and_renewals/count_loans_and_renewals.sql";
    String remoteSQL = templateQueryService.fetchRemoteSQL(url);
    String functionName = templateQueryService.getSQLFunctionName(remoteSQL);
    Map<String, String> params = new HashMap<>();
    params.put("start-date", "2020-12-03");
    params.put("username", "Big Howie");
    assertEquals("count_loans_and_renewals", functionName);

  }

  @Test
  public void testBadFunctionName() throws Exception {
    String badSql = "badsql";
    String functionName = templateQueryService.getSQLFunctionName(badSql);
    assertNull(functionName);
  }


  @Test
  public void testBasicFunction() throws Exception {
    /*
      {
        "url": "https://gist.githubusercontent.com/kurtnordstrom/f04d44cdaf258bfc1a78de35582c51d0/raw/a2411255a7492d062439331a8798010d38168ddf/get_users.sql",
        "params": {
          "start_date": "2016-08-18T00:00:00.000Z",
          "end_date": "2023-08-18T00:00:00.000Z"
        }
      }
     */
    String jsonString = "{ \"url\": \"https://gist.githubusercontent.com/kurtnordstrom/f04d44cdaf258bfc1a78de35582c51d0/raw/e17fb5f64bbe2af3cceffb33510ab8d0b8dcdd78/get_users.sql\", \"params\": { \"start_date\": \"2016-08-18T00:00:00.000Z\", \"end_date\": \"2023-08-18T00:00:00.000Z\" } }";
    mvc.perform(post(QUERY_PATH)
            .contentType("application/json")
            .header("X-Okapi-Tenant", "diku")
            .content(jsonString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records", hasSize(3)))
        .andExpect(jsonPath("$.totalRecords", is(3)));

  }

  @Test
  public void testBasicFunctionWithLimit() throws Exception {
    /*
      {
        "url": "https://gist.githubusercontent.com/kurtnordstrom/f04d44cdaf258bfc1a78de35582c51d0/raw/a2411255a7492d062439331a8798010d38168ddf/get_users.sql",
        "params": {
          "start_date": "2016-08-18T00:00:00.000Z",
          "end_date": "2023-08-18T00:00:00.000Z"
        },
        "limit": 1
      }
     */
    String jsonString = "{ \"url\": \"https://gist.githubusercontent.com/kurtnordstrom/f04d44cdaf258bfc1a78de35582c51d0/raw/e17fb5f64bbe2af3cceffb33510ab8d0b8dcdd78/get_users.sql\", \"params\": { \"start_date\": \"2016-08-18T00:00:00.000Z\", \"end_date\": \"2023-08-18T00:00:00.000Z\" }, \"limit\": 1 }";
    mvc.perform(post(QUERY_PATH)
            .contentType("application/json")
            .header("X-Okapi-Tenant", "diku")
            .content(jsonString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records", hasSize(1)))
        .andExpect(jsonPath("$.totalRecords", is(1)));

  }

  @Test
  public void testRollback() throws Exception {
    DriverManagerDataSource dmds = new DriverManagerDataSource(externalPostgreSQLContainer.getJdbcUrl(),
        externalPostgreSQLContainer.getUsername(), externalPostgreSQLContainer.getPassword());
    String sql = "SELECT barcode from user_users WHERE id = ?";
    String idVal = "00bc2807-4d5b-4a27-a2b5-b7b1ba431cc4";
    String sqlUpdate = "UPDATE user_users SET barcode = ? where id = ?";
    dmds.setDriverClassName("org.postgresql.Driver");
    JdbcTemplate jdbc = new JdbcTemplate(dmds);
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dmds);
    String barcode = null;
    TransactionStatus transactionStatus
        = transactionManager.getTransaction(new DefaultTransactionDefinition());
    transactionStatus.setRollbackOnly();
    barcode = jdbc.queryForObject(sql, new Object[]{idVal}, String.class);
    assertEquals("133143370961512", barcode);
    jdbc.update(sqlUpdate, "777777", idVal);
    barcode = jdbc.queryForObject(sql, new Object[]{idVal}, String.class);
    assertEquals("777777", barcode);
    if(!transactionStatus.isCompleted()) {
      transactionManager.rollback(transactionStatus);
    }
    //attempt query post rollback
    barcode = jdbc.queryForObject(sql, new Object[]{idVal}, String.class);
    assertEquals("133143370961512", barcode);

  }


}
