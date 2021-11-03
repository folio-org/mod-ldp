package org.folio.ldp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public class CrawlTableObjRepository {
  private JdbcTemplate jdbc;

  public void setJdbcTemplate(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<TableObj> getAllTablesBySchema() {

    ArrayList<String> schemaList = new ArrayList<>();
    schemaList.add("local");
    schemaList.add("folio_reporting");
    schemaList.add("public");

    try {
      return SchemaUtil.getTablesBySchemaName(jdbc, schemaList);
    } catch(Exception e) {
      System.out.println("Error getting tables: " + e.getLocalizedMessage());
      throw e;
    }
  }

  public List<TableObj> findAll() {
    return getAllTablesBySchema();
  }

  

}
