package org.folio.ldp;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchemaUtil {


  public static List<TableObj> getTablesBySchemaName(JdbcTemplate jdbc, List<String> schemaNameList) {
    String query = "SELECT * from information_schema.tables";
    if(schemaNameList == null) {
      schemaNameList = new ArrayList<>();
    }
    if(!schemaNameList.isEmpty()) {
      String inClause = String.join(",", Collections.nCopies(schemaNameList.size(), "?"));
      query = query + " WHERE table_schema IN (" + inClause + ")";
    }
    return jdbc.query(query, schemaNameList.toArray(), new RowMapper<TableObj>() {
      public TableObj mapRow(ResultSet rs, int rowNumber) throws SQLException {
        TableObj tableObj = new TableObj();
        tableObj.setTableName(rs.getString("table_name"));
        tableObj.setTableSchema(rs.getString("table_schema"));
        return tableObj;
      }
    });
  }


  public static List<ColumnObj> getColumnsByTableName(JdbcTemplate jdbc, String schemaName,
   String tableName) {
     String query = "select * from information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name != ?";
     return jdbc.query(query, new String[] {schemaName, tableName, "data"}, new RowMapper<ColumnObj>() {
       public ColumnObj mapRow(ResultSet rs, int rowNumber) throws SQLException {
         ColumnObj columnObj = new ColumnObj();
         columnObj.setColumnName(rs.getString("column_name"));
         columnObj.setData_type(rs.getString("data_type"));
         columnObj.setOrdinalPosition(rs.getString("ordinal_position"));
         columnObj.setTableName(tableName);
         columnObj.setTableSchema(schemaName);
         
         return columnObj;
       }

     });

   }
  
}
