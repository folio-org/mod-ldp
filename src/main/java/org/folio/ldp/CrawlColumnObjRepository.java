package org.folio.ldp;

import java.sql.Connection;

import java.util.List;

public class CrawlColumnObjRepository {
  private Connection connection;

  public void setConnection(Connection c) {
    connection = c;
  }

  List<ColumnObj> findByTableName(String schema, String table) {

    List<ColumnObj> colList = null;

    try {
      colList = SchemaUtil.getColumnsByTableName(connection, schema, table);
    } catch(Exception e) {
      System.out.println("Error getting columns: " + e.getLocalizedMessage());
    }

    return colList;    
  }
  
  
}
