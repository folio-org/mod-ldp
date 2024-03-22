package org.folio.ldp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

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

  private JdbcTemplate jdbc;

  @Autowired
  private TableObjController tableController;

  @Autowired
  private QueryService queryService;

  @Autowired
  private DBInfoService dbInfoService;

  @PostMapping
  public List<Map<String, Object>> postQuery(@RequestBody QueryObj queryObj, HttpServletResponse response) {
    String tenantId = TenantContext.getCurrentTenant();
    Map<String, String> dbMap;

    dbMap = dbInfoService.getDBInfo(tenantId);


    if(dbMap == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Unable to get database connection information. Make sure the values are populated");
    }

    DriverManagerDataSource dmds;
    Boolean isMetadb;

    try{
      dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"), dbMap.get("pass"));
      dmds.setDriverClassName("org.postgresql.Driver");
      jdbc = new JdbcTemplate(dmds);
      isMetadb = !SchemaUtil.isLDP(jdbc);
    } catch(Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Unable to get database connection: "
        + e.getLocalizedMessage() );
    }

    TableQuery tableQuery = queryObj.tables.get(0);

    if(tableQuery == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tables` is required");
    }

    ArrayList<String> schemaWhitelist = new ArrayList<String>(Arrays.asList("public", "local", "folio_reporting"));
    if(!isMetadb && !schemaWhitelist.contains(tableQuery.schema)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `schema` value '" + tableQuery.schema +
        "' not found in whitelist: "+ schemaWhitelist);
    }

    if(tableQuery.tableName.equals("")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tableName` is required");
    }

    if(tableQuery.tableName.length() > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Parameter `tableName` is too long");
    }

    // Validate table name as whitelisted
    Map<String, Boolean> tables = tableController.getTablesAsMap();
    Boolean isValidTableName = tables.get(tableQuery.tableName);
    if(isValidTableName == null) {
      String validTables = String.join(",", tables.keySet());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error for parameter `tableName`: `"+ tableQuery.tableName + "` is not a valid table name, valid table names are " + validTables  );
    }

    // TODO: switch to this validation
    // DbSpec spec = new DbSpec();
    // DbSchema schema = spec.addDefaultSchema();
    // DbTable customerTable = schema.addTable(query.tableName);
    // DbColumn custIdCol = customerTable.addColumn("cust_id", "number", null);

    // Map<String, String> availableColumns = columnController.getColumnsForTableAsMap(query.schema, query.tableName);
    // System.out.println(availableColumns);

    String queryContent = queryService.generateQuery(tableQuery);

    List<Map<String, Object>> content;
    try {
      content = jdbc.query(queryContent, new ResultSetExtractor<List<Map<String, Object>>>() {
        public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
              LinkedHashMap<String, Object> row = new LinkedHashMap<>();
              for (int i = 1; i <= columnCount; i++) {
                // Note that the index is 1-based
                String colName = rsmd.getColumnName(i);

                if(colName.equals("data")) { continue; }

                Object colVal = rs.getObject(i);
                row.put(colName, colVal);
              }
              rows.add(row);
            }
            return rows;
          }
      });
    } catch(Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error making query: " + e.getLocalizedMessage());
    }
    return content;
  }
}
