package org.folio.ldp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/ldp/db/query", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryController {
  @Autowired private JdbcTemplate jdbc;
  @Autowired private TableObjController tableController;
  @Autowired private ColumnObjController columnController;

  @PostMapping
  public List<Map<String, Object>> postQuery(@RequestBody QueryObj queryObj, HttpServletResponse response) {
    TableQuery query = queryObj.tables.get(0);
    if(query.tableName == "") {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tableName` is required");
    }
    if(query.tableName.length() > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tableName` is too long");
    }
    // Validate table name as whitelisted
    Map<String, Boolean> tables = tableController.getTablesAsMap();
    Boolean isValidTableName = tables.get(query.tableName);
    if(isValidTableName == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error for parameter `tableName`: `"+ query.tableName + "` is not a valid table name");
    }

    // TODO: switch to this validation
    // DbSpec spec = new DbSpec();
    // DbSchema schema = spec.addDefaultSchema();
    // DbTable customerTable = schema.addTable(query.tableName);
    // DbColumn custIdCol = customerTable.addColumn("cust_id", "number", null);
    
    Map<String, String> availableColumns = columnController.getColumnsForTableAsMap(query.tableName);
    SelectQuery selectQuery =
      (new SelectQuery())
      .addAllColumns()
      .addCustomFromTable(query.tableName);
    
    for (ColumnFilter col : query.columns) {
      if(col == null || col.key == "" || col.key == null || col.value == "" || col.value == null) { continue; }
      selectQuery = selectQuery.addCondition(BinaryCondition.equalTo(new CustomSql(col.key), col.value));
    }

    String selectQueryStr = selectQuery.validate().toString();

    System.out.println("QUERYSTART");
    System.out.println(selectQueryStr);
    String rawQueryContent = selectQueryStr;

    // 1) ✅ Read user-provided column:values from request body
    // 2) Validate user columns with whitelisted column names
    // 3) 🖍️ Build SQL string w/ verified columns
    // 4) Create prepared statement with SQL string
    //      PreparedStatement ps = conn.prepareStatement(query);
    // 5) SetString() for each value, eliminating SQL injection
    //      ps.setString(1,list.get(0));
    

    final String queryContent;
    if (!rawQueryContent.toLowerCase().contains("limit")) {
      queryContent = rawQueryContent + " LIMIT 500";
    } else {
      queryContent = rawQueryContent;
    }
    List<Map<String, Object>> content = jdbc.query(queryContent, new ResultSetExtractor<List<Map<String, Object>>>() {
      public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {

          List<Map<String, Object>> rows = new ArrayList<>();
          ResultSetMetaData rsmd = rs.getMetaData();
          int columnCount = rsmd.getColumnCount();
          
          while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
              // Note that the index is 1-based
              String colName = rsmd.getColumnName(i);

              if(colName == "data") { continue; }

              Object colVal = rs.getObject(i);
              row.put(colName, colVal);
            }
            rows.add(row);
          }
          return rows;
        }      
    });
    return content;
  }
}
