package org.folio.ldp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import jdk.jfr.Timestamp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.After;
import org.junit.Test;

import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
  "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
public class QueryControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private QueryController queryController;

  @Autowired
  private QueryService queryService;

  public final static String QUERY_PATH = "/ldp/db/query";

  @Test
  public void testTest() {
    
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
        .content(jsonString))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(3)));
  
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


}
