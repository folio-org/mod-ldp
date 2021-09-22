package org.folio.ldp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.jdbc.Sql;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.ClassRule;

import org.testcontainers.containers.PostgreSQLContainer;

import schemacrawler.schema.Column;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {SchemaColumnTest.Initializer.class})
@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})
@AutoConfigureMockMvc
public class SchemaColumnTest {

  @ClassRule
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:12-alpine")
    .withDatabaseName("integration-tests-db")
    .withUsername("sa")
    .withPassword("sa");

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
  private ApplicationContext context;

  @Test
  public void getColumns() throws Exception {
    DataSource ds = (DataSource)context.getBean("dataSource");
    Connection conn = ds.getConnection();
    List<Column> columnList = SchemaColumnUtil.getColumnsByTableName(conn, "public", "user_users");
    assertEquals(11, columnList.size());
  }
  
}
