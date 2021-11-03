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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.ClassRule;

import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {ConfigObjControllerTest.Initializer.class})
@AutoConfigureMockMvc
public class ConfigObjControllerTest {
  @ClassRule
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("configobj-integration-tests-db")
    .withUsername("sa")
    .withPassword("sa");

  static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
          TestPropertyValues.of(
            "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
            "spring.datasource.username=" + postgreSQLContainer.getUsername(),
            "spring.datasource.password=" + postgreSQLContainer.getPassword(),
            "dbinfo.url=dummy",
            "dbinfo.user=dummy",
            "dbinfo.pass=dummy"
          ).applyTo(configurableApplicationContext.getEnvironment());
      }
  }
  
  @Autowired private MockMvc mvc;

  @Autowired ConfigObjRepository repo;

  public final static String QUERY_PATH = "/ldp/config";

  @Test
  public void setAndRetrieveMVC() throws Exception {
    JSONObject dbconf = new JSONObject();
    dbconf.put("url", postgreSQLContainer.getJdbcUrl());
    dbconf.put("user", postgreSQLContainer.getUsername());
    dbconf.put("pass", postgreSQLContainer.getPassword());
    JSONObject json = new JSONObject();
    json.put("key", "dbconf");
    json.put("value", dbconf);

    mvc.perform(put(QUERY_PATH + "/" + "dbconf")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().isOk());

    mvc.perform(get(QUERY_PATH + "/" + "dbconf")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku"))
        .andExpect(status().isOk());

  }

  @Test
  public void setAndOverwriteMVC() throws Exception {
    JSONObject dbconf = new JSONObject();
    dbconf.put("url", postgreSQLContainer.getJdbcUrl());
    dbconf.put("user", postgreSQLContainer.getUsername());
    dbconf.put("pass", postgreSQLContainer.getPassword());
    JSONObject json = new JSONObject();
    json.put("key", "dbconf");
    json.put("value", dbconf);

    mvc.perform(put(QUERY_PATH + "/" + "dbconf")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().isOk());

    //Do it again
    dbconf.put("dummy", "dummy");

    mvc.perform(put(QUERY_PATH + "/" + "dbconf")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().isOk());

  }

  @Test
  public void putInvalidKey() throws Exception {
    JSONObject valueJson = new JSONObject();
    valueJson.put("name","doofensmirch");
    valueJson.put("role","villain");
    JSONObject json = new JSONObject();
    json.put("key", "npc");
    json.put("value", valueJson.toString());

    mvc.perform(put(QUERY_PATH + "/" + "stupid")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().is(400));
  }

  @Test
  public void putBadJson() throws Exception {
    mvc.perform(put(QUERY_PATH + "/" + "stupid")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content("{\"key\":\"stupid\", \"value\": \"something"))
        .andExpect(status().is(400));
  }

  @Test
  public void putBadJsonKey() throws Exception {
    JSONObject valueJson = new JSONObject();
    valueJson.put("name","doofensmirch");
    valueJson.put("role","villain");
    JSONObject json = new JSONObject();
    json.put("key", "npc");
    json.put("value", "{\"dog\":\"woof");

    mvc.perform(put(QUERY_PATH + "/" + "npc")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().is(400));
  }

  @Test
  public void testRepo() throws Exception {
    ConfigObj config = new ConfigObj();
    JSONObject json = new JSONObject();
    json.put("foo", "pickle");
    json.put("bar", "tomato");
    config.setTenant("diku");
    config.setKey("foobar");
    config.setValue(json);
    repo.save(config);

    ConfigObjId configId = new ConfigObjId();
    configId.setTenant("diku");
    configId.setKey("foobar");

    ConfigObj newConfig = repo.findById(configId).get();
    assertNotNull(newConfig);
    assertTrue(newConfig.getValue().getString("bar").equals("tomato"));
    assertTrue(newConfig.getValue().getString("foo").equals("pickle"));

  }


  @Test
  public void testHybrid() throws Exception {
    JSONObject dbconf = new JSONObject();
    dbconf.put("url", postgreSQLContainer.getJdbcUrl());
    dbconf.put("user", postgreSQLContainer.getUsername());
    dbconf.put("pass", postgreSQLContainer.getPassword());
    JSONObject json = new JSONObject();
    json.put("key", "dbconf");
    json.put("value", dbconf.toString());

    mvc.perform(put(QUERY_PATH + "/" + "dbconf")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "indexdata")
      .content(json.toString()))
        .andExpect(status().isOk());
    
    ConfigObjId configId = new ConfigObjId();
    configId.setTenant("indexdata");
    configId.setKey("dbconf");

    ConfigObj newConfig = repo.findById(configId).get();
    assertNotNull(newConfig);
    assertTrue(newConfig.getValue().getString("url").equals(postgreSQLContainer.getJdbcUrl()));
    assertTrue(newConfig.getValue().getString("user").equals(postgreSQLContainer.getUsername()));
  }

  
}
