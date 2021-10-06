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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.ClassRule;

import org.json.JSONObject;

import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {QueryControllerTest.Initializer.class})
//@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})
@AutoConfigureMockMvc
public class QueryControllerTest {

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
  private QueryController queryController;

  @Autowired
  private ConfigObjRepository configObjRepo;

  @Autowired
  private QueryService queryService;

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
  public void queryAllUsers() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "public",
            "tableName": "user_users",
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
    "{\"tables\":[{\"schema\":\"public\",\"tableName\":\"user_users\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku") 
      .content(jsonString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));

  }

  @Test
  public void queryAllUsersOrdered() throws Exception {
    /*
    Payload:
    {
      "tables": [
        {
          "schema": "public",
          "tableName": "user_users",
          "columnFilters": [
            {
              "key": "active",
              "value": "false"
            }
          ],
          "showColumns": [
            "active",
            "barcode",
            "type",
            "username"
          ],
          "orderBy": [
            {
              "key": "username",
              "direction": "asc",
              "nulls": "end"
            },
            {
              "key": "barcode",
              "direction": "desc",
              "nulls": "start"
            }
          ],
          "limit": "1000"
        }
      ]
    }
    */
    
    String jsonString = "{\"tables\":[{\"schema\":\"public\",\"tableName\":\"user_users\",\"columnFilters\":[{\"key\":\"active\",\"value\":\"false\"}],\"showColumns\":[\"active\",\"barcode\",\"type\",\"username\"],\"orderBy\":[{\"key\":\"username\",\"direction\":\"asc\",\"nulls\":\"end\"},{\"key\":\"barcode\",\"direction\":\"desc\",\"nulls\":\"start\"}],\"limit\":\"1000\"}]}" ;
    mvc.perform(
      post(QUERY_PATH)
        .contentType("application/json")
        .header("X-Okapi-Tenant", "diku")
        .content(jsonString))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(3)));
  
  }

  @Test
  public void queryBadSchema() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "doodoo",
            "tableName": "user_users",
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
    "{\"tables\":[{\"schema\":\"doodoo\",\"tableName\":\"user_users\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(jsonString))
        .andExpect(status().is4xxClientError());

  }

  @Test
  public void queryBadTable() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "public",
            "tableName": "doodoo_doodooos",
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
    "{\"tables\":[{\"schema\":\"doodoo\",\"tableName\":\"doodoo_doodoos\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(jsonString))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void queryBadTableLength() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "public",
            "tableName": "toooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobig",
            "columnFilters": [
              {}
            ],
            "showColumns": [],
            "orderBy": [],
            "limit": 10000
          }
        ]
      }
    */
    String jsonString = 
    "{\"tables\":[{\"schema\":\"doodoo\",\"tableName\":\"toooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobigtoooooooooooooobig\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(jsonString))
        .andExpect(status().is4xxClientError());

  }

  @Test
  public void queryNoTable() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "public",
            "tableName": "",
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
    "{\"tables\":[{\"schema\":\"doodoo\",\"tableName\":\"\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(jsonString))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void queryNoLimit() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "public",
            "tableName": "user_users",
            "columnFilters": [
              {}
            ],
            "showColumns": [],
            "orderBy": []
          }
        ]
      }
    */
    String jsonString = 
    "{\"tables\":[{\"schema\":\"public\",\"tableName\":\"user_users\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[]}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(jsonString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
        //probably check to see limit changed to 500?

  }


  @Test 
  public void testQueryGeneration() throws Exception {
    TableQuery tableQuery = new TableQuery();
    tableQuery.schema = "public";
    tableQuery.tableName = "inventory_instances";
    List<ColumnFilter> columnFilters = new ArrayList<>();
    List<String> showColumns = new ArrayList<>();
    List<OrderingCriterion> orderBy = new ArrayList<>();
    columnFilters.add(new ColumnFilter("_version", "2"));
    columnFilters.add(new ColumnFilter("source", "MARC"));
    showColumns.add("_version");
    showColumns.add("hrid");
    showColumns.add("index_title");
    showColumns.add("source");
    showColumns.add("status_updated_date");
    orderBy.add(new OrderingCriterion("index_title", "asc", "end"));
    orderBy.add(new OrderingCriterion("status_updated_date", "desc", "end"));
    tableQuery.columnFilters = columnFilters;
    tableQuery.showColumns = showColumns;
    tableQuery.orderBy = orderBy;
    tableQuery.limit = 1001;

    String query = queryService.generateQuery(tableQuery);
   
    final String expectedQuery = "SELECT \"_version\",\"hrid\",\"index_title\",\"source\",\"status_updated_date\" FROM public.inventory_instances WHERE ((\"_version\" = '2') AND (\"source\" = 'MARC')) ORDER BY \"index_title\" ASC NULLS LAST,\"status_updated_date\" DESC NULLS LAST LIMIT 1001";

    assertEquals(query, expectedQuery);  

  }

  @Test 
  public void testQueryGenNoColumns() throws Exception {
    TableQuery tableQuery = new TableQuery();
    tableQuery.schema = "public";
    tableQuery.tableName = "inventory_instances";
    
    List<String> showColumns = new ArrayList<>();
    List<OrderingCriterion> orderBy = new ArrayList<>();
    
    showColumns.add("_version");
    showColumns.add("hrid");
    showColumns.add("index_title");
    showColumns.add("source");
    showColumns.add("status_updated_date");
    orderBy.add(new OrderingCriterion("index_title", "asc", "end"));
    orderBy.add(new OrderingCriterion("status_updated_date", "desc", "end"));
    
    tableQuery.showColumns = showColumns;
    tableQuery.orderBy = orderBy;
    tableQuery.limit = 1001;

    String query = queryService.generateQuery(tableQuery);

    assertNotNull(query);
    assertNotEquals("", query);
  }

  @Test
  public void queryBadTenant() throws Exception{
    /*
    Payload:

        {
        "tables": [
          {
            "schema": "public",
            "tableName": "user_users",
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
    "{\"tables\":[{\"schema\":\"public\",\"tableName\":\"user_users\",\"columnFilters\":[{}],\"showColumns\":[],\"orderBy\":[],\"limit\":101}]}";
    mvc.perform(post(QUERY_PATH)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "deeeeku") 
      .content(jsonString))
        .andExpect(status().is(500));

  }


}
