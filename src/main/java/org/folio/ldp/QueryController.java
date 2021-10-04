package org.folio.ldp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/ldp/db/query", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryController {

  //@Autowired 
  private JdbcTemplate jdbc;
  
  @Autowired
  private TableObjController tableController;
  //@Autowired private ColumnObjController columnController;
  
  @Autowired 
  private QueryService queryService;
  
  @Autowired
  private DBInfoService dbInfoService;

  @PostMapping
  public List<Map<String, Object>> postQuery(@RequestBody QueryObj queryObj, HttpServletResponse response) {
    Map<String, String> dbMap = dbInfoService.getDBInfo();
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");

    jdbc = new JdbcTemplate(dmds);
    
    TableQuery query = queryObj.tables.get(0);

    ArrayList<String> schemaWhitelist = new ArrayList<String>(Arrays.asList("public", "local", "folio_reporting"));
    if(!schemaWhitelist.contains(query.schema)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `schema` value '" + query.schema +
        "' not found in whitelist: "+ schemaWhitelist);
    }

    if(query.tableName.equals("")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tableName` is required");
    }

    if(query.tableName.length() > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tableName` is too long");
    }

    // Validate table name as whitelisted
    Map<String, Boolean> tables = tableController.getTablesAsMap();
    Boolean isValidTableName = tables.get(query.tableName);
    if(isValidTableName == null) {
      String validTables = String.join(",", tables.keySet());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error for parameter `tableName`: `"+ query.tableName + "` is not a valid table name, valid table names are " + validTables  );
    }

    // TODO: switch to this validation
    // DbSpec spec = new DbSpec();
    // DbSchema schema = spec.addDefaultSchema();
    // DbTable customerTable = schema.addTable(query.tableName);
    // DbColumn custIdCol = customerTable.addColumn("cust_id", "number", null);

    // Map<String, String> availableColumns = columnController.getColumnsForTableAsMap(query.schema, query.tableName);
    // System.out.println(availableColumns);

    String queryContent = queryService.generateQuery(query);

    List<Map<String, Object>> content = jdbc.query(queryContent, new ResultSetExtractor<List<Map<String, Object>>>() {
      public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {

          List<Map<String, Object>> rows = new ArrayList<>();
          ResultSetMetaData rsmd = rs.getMetaData();
          int columnCount = rsmd.getColumnCount();

          while (rs.next()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
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
