package org.folio.ldp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.junit.After;
import org.junit.Test;
import org.junit.ClassRule;
import org.junit.Ignore;
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
    JSONObject jsonobj = new JSONObject();
    jsonobj.put("species", "dog");
    jsonobj.put("age", 4);
    jsonobj.put("name", "Sparky");
    JSONObject json = new JSONObject();
    json.put("key", "animal");
    json.put("value", jsonobj);

    mvc.perform(put(QUERY_PATH + "/" + "animal")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().isOk());
        

    MvcResult mvcResult = mvc.perform(get(QUERY_PATH + "/" + "animal")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.key", is("animal")))
        .andReturn();
    
    String content = mvcResult.getResponse().getContentAsString();
    //System.out.println("Got content from request: " + content);
    JSONObject resultJson = (JSONObject) JSONValue.parse(content);
    JSONObject valueJson = (JSONObject) JSONValue.parse((String)resultJson.get("value"));
    assertEquals("dog", (String)valueJson.get("species"));

  }

  @Test
  public void setAndRetrieveMVCDBInfo() throws Exception {
    JSONObject dbinfo = new JSONObject();
    dbinfo.put("url", postgreSQLContainer.getJdbcUrl());
    dbinfo.put("user", postgreSQLContainer.getUsername());
    dbinfo.put("pass", postgreSQLContainer.getPassword());
    JSONObject json = new JSONObject();
    json.put("key", "dbinfo");
    json.put("value", dbinfo);

    mvc.perform(put(QUERY_PATH + "/" + "dbinfo")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(json.toString()))
        .andExpect(status().isOk());

    MvcResult mvcResult = mvc.perform(get(QUERY_PATH + "/" + "dbinfo")
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku"))
        .andExpect(status().isOk())
        .andReturn();
    
    String content = mvcResult.getResponse().getContentAsString();
    System.out.println("Got content from request: " + content);
    JSONObject resultJson = (JSONObject) JSONValue.parse(content);
    JSONObject valueJson = (JSONObject) JSONValue.parse((String)resultJson.get("value"));
    assertEquals("", (String)valueJson.get("pass"));

    //Make sure we've got the right value in the repo
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant("diku");
    configObjId.setKey("dbinfo");
    ConfigObj dbinfoConfig = repo.findById(configObjId).get();
    assertEquals(dbinfoConfig.getValue().get("pass"), postgreSQLContainer.getPassword());

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
      .content(json.toJSONString()))
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
    assertTrue(newConfig.getValue().get("bar").equals("tomato"));
    assertTrue(newConfig.getValue().get("foo").equals("pickle"));

  }


  @Test
  public void testHybrid() throws Exception {
    JSONObject dbconf = new JSONObject();
    dbconf.put("url", postgreSQLContainer.getJdbcUrl());
    dbconf.put("user", postgreSQLContainer.getUsername());
    dbconf.put("pass", postgreSQLContainer.getPassword());
    JSONObject json = new JSONObject();
    json.put("key", "dbconf");
    json.put("value", dbconf);

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
    assertTrue(newConfig.getValue().get("url").equals(postgreSQLContainer.getJdbcUrl()));
    assertTrue(newConfig.getValue().get("user").equals(postgreSQLContainer.getUsername()));
  }

  @Test
  public void testOverwriteDbinfoWithEmptyPass() throws Exception {
    ConfigObj configObj = new ConfigObj();
    JSONObject json = new JSONObject();
    String key = "dbinfo";
    
    json.put("url", postgreSQLContainer.getJdbcUrl());
    json.put("user", postgreSQLContainer.getUsername());
    json.put("pass", postgreSQLContainer.getPassword());
    configObj.setTenant("diku");
    configObj.setKey(key);
    configObj.setValue(json);
    repo.save(configObj);
    
    JSONObject putJson = new JSONObject();
    JSONObject newDbinfo = new JSONObject();
    newDbinfo.put("url", postgreSQLContainer.getJdbcUrl());
    newDbinfo.put("user", postgreSQLContainer.getUsername());
    newDbinfo.put("pass", "");
    putJson.put("key", key);
    putJson.put("value", newDbinfo);

    mvc.perform(put(QUERY_PATH + "/" + key)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(putJson.toJSONString()))
        .andExpect(status().isOk());
    
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant("diku");
    configObjId.setKey(key);
    ConfigObj dbinfoConfig = repo.findById(configObjId).get();
    assertEquals(dbinfoConfig.getValue().get("pass"), postgreSQLContainer.getPassword());


  }

  @Test
  public void testOverwriteDbinfoWithPass() throws Exception {
    ConfigObj configObj = new ConfigObj();
    JSONObject json = new JSONObject();
    String key = "dbinfo";
    
    json.put("url", postgreSQLContainer.getJdbcUrl());
    json.put("user", postgreSQLContainer.getUsername());
    json.put("pass", postgreSQLContainer.getPassword());
    configObj.setTenant("diku");
    configObj.setKey(key);
    configObj.setValue(json);
    repo.save(configObj);
    
    JSONObject putJson = new JSONObject();
    JSONObject newDbinfo = new JSONObject();
    String newPass = "royale_with_cheese";
    newDbinfo.put("url", postgreSQLContainer.getJdbcUrl());
    newDbinfo.put("user", postgreSQLContainer.getUsername());
    newDbinfo.put("pass", newPass);
    putJson.put("key", key);
    putJson.put("value", newDbinfo);

    mvc.perform(put(QUERY_PATH + "/" + key)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(putJson.toJSONString()))
        .andExpect(status().isOk());
    
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant("diku");
    configObjId.setKey(key);
    ConfigObj dbinfoConfig = repo.findById(configObjId).get();
    assertEquals(dbinfoConfig.getValue().get("pass"), newPass);


  }

  @Test
  public void testOverwriteSqConfigWithEmptyToken() throws Exception {
    ConfigObj configObj = new ConfigObj();
    JSONObject json = new JSONObject();
    String key = "sqconfig";
    
    json.put("token", "goodtoken");
    json.put("foo","bar");
    configObj.setTenant("diku");
    configObj.setKey(key);
    configObj.setValue(json);
    repo.save(configObj);
    
    JSONObject putJson = new JSONObject();
    JSONObject newSQConfig = new JSONObject();
    newSQConfig.put("token", "");
    newSQConfig.put("foo","bar");
    putJson.put("key", key);
    putJson.put("value", newSQConfig);

    mvc.perform(put(QUERY_PATH + "/" + key)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(putJson.toJSONString()))
        .andExpect(status().isOk());
    
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant("diku");
    configObjId.setKey(key);
    ConfigObj sqConfigConfig = repo.findById(configObjId).get();
    assertEquals(sqConfigConfig.getValue().get("token"), "goodtoken");


  }

  @Test
  public void testOverwriteSqConfigWithToken() throws Exception {
    ConfigObj configObj = new ConfigObj();
    JSONObject json = new JSONObject();
    String key = "sqconfig";
    
    json.put("token", "goodtoken");
    
    configObj.setTenant("diku");
    configObj.setKey(key);
    configObj.setValue(json);
    repo.save(configObj);
    
    JSONObject putJson = new JSONObject();
    JSONObject newSqConfig = new JSONObject();
    String newToken = "royale_with_cheese";
    
    newSqConfig.put("token", newToken);

    putJson.put("key", key);
    putJson.put("value", newSqConfig);

    mvc.perform(put(QUERY_PATH + "/" + key)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(putJson.toJSONString()))
        .andExpect(status().isOk());
    
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant("diku");
    configObjId.setKey(key);
    ConfigObj sqConfigConfig = repo.findById(configObjId).get();
    assertEquals(sqConfigConfig.getValue().get("token"), newToken);


  }

  

  //@Ignore
  @Test
  public void testPutWithBadJsonFormat() throws Exception {
   
    String key = "wtf";
    JSONObject putJson = new JSONObject();
    JSONObject randoJson = new JSONObject();
    randoJson.put("what", "I dunno");
    randoJson.put("the", "You dunno");
    randoJson.put("frick", "We dunno");
    putJson.put("KEY", key);
    putJson.put("VALUE", randoJson);

    mvc.perform(put(QUERY_PATH + "/" + key)
      .contentType("application/json")
      .header("X-Okapi-Tenant", "diku")
      .content(putJson.toJSONString()))
        .andExpect(status().isBadRequest());

  }

  
}
