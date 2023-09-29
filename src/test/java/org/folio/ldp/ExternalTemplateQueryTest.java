package org.folio.ldp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExternalTemplateQueryTest {

  @Autowired
  private TemplateQueryController templateQueryController;

  @Autowired
  private TemplateQueryService templateQueryService;

  @Test
  public void testFromFileParams() throws Exception {
    String[] testList = { "externalDbTest.json" };
    List<String> jsonStringList = new ArrayList<>();

    for (String filename : testList) {
      String fileText = null;
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      if (classLoader.getResource(filename) != null) {
        fileText =
            new Scanner(classLoader.getResourceAsStream(filename), "UTF-8")
                .useDelimiter("\\A").next();
        jsonStringList.add(fileText);
      }
      for (String jsonString : jsonStringList) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        assertNotNull(jsonObject);

        String dbUrl = (String) jsonObject.get("dbUrl");
        String dbUser = (String) jsonObject.get("dbUser");
        String dbPassword = (String) jsonObject.get("dbPassword");

        JSONObject templateQueryParams = (JSONObject) jsonObject.get("templateQueryParams");

        Integer limit = templateQueryParams.get("limit") != null ? ((Long) templateQueryParams.get("limit")).intValue(): null;
        String url = (String) templateQueryParams.get("url");
        JSONObject queryParams = (JSONObject) templateQueryParams.get("params");

        Map<String, String> paramMap = new HashMap<>();
        for( Object key : queryParams.keySet()) {
          String value = (String) queryParams.get(key);
          paramMap.put( (String)key, value);
        }

        TemplateQueryObj templateQueryObj = new TemplateQueryObj();
        templateQueryObj.params = paramMap;
        templateQueryObj.url = url;
        templateQueryObj.limit = limit;
        System.out.println("limit is: " + (limit != null ? limit.toString() : "null"));


        DriverManagerDataSource dmds = new DriverManagerDataSource(dbUrl, dbUser,
            dbPassword);

        dmds.setDriverClassName("org.postgresql.Driver");

        Map<String, Object> resultMap
            = templateQueryController.executePostTemplateQueryInTransaction(dmds, templateQueryObj);
        assertEquals(1, (Integer)(resultMap.get("totalRecords")));
      }
    }

  }
}
