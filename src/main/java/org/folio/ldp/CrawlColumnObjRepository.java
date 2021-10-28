package org.folio.ldp;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public class CrawlColumnObjRepository {
  private JdbcTemplate jdbc;

  public void setJdbcTemplate(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  List<ColumnObj> findByTableName(String schema, String table) {

    List<ColumnObj> colList = null;

    try {
      colList = SchemaUtil.getColumnsByTableName(jdbc, schema, table);
    } catch(Exception e) {
      System.out.println("Error getting columns: " + e.getLocalizedMessage());
    }

    return colList;    
  }
  
  
}
