package org.folio.ldp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CrawlTableObjRepository {
  private Connection connection;

  public void setConnection(Connection c) {
    connection = c;
  }

  public List<TableObj> getAllTablesBySchema() {

    ArrayList<String> schemaList = new ArrayList<>();
    schemaList.add("local");
    schemaList.add("folio_reporting");
    schemaList.add("public");

    try {
      return SchemaUtil.getTablesBySchemaName(connection, schemaList);
    } catch(Exception e) {
      System.out.println("Error getting tables: " + e.getLocalizedMessage());
      return null;
    }
  }

  public List<TableObj> findAll() {
    try {
      return SchemaUtil.getTablesBySchemaName(connection, null);
    } catch(Exception e) {
      System.out.println("Error getting tables: " + e.getLocalizedMessage());
      return null;
    }
  }

  

}
