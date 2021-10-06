package org.folio.ldp;

import java.util.TreeMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ldp/db/columns")
public class ColumnObjController {
  
  CrawlColumnObjRepository columnRepository = new CrawlColumnObjRepository();

  @Autowired DBInfoService dbInfoService;

  @GetMapping

  public List<ColumnObj> getColumnsForTable(String schema, String table)  {

    // TODO: Validate schema and table strings
    try {
      columnRepository.setConnection(getConnection());
    } catch(Exception e) {
      System.out.println("Error getting connection: " + e.getLocalizedMessage());
      return null;
    }
    return (List<ColumnObj>) columnRepository.findByTableName(schema, table);
  }

  @Cacheable(cacheNames="columns")
  public Map<String, String> getColumnsForTableAsMap(String schema, String table) {

    // TODO: Validate table string
    try {
      columnRepository.setConnection(getConnection());
    } catch(Exception e) {
      System.out.println("Error getting connection: " + e.getLocalizedMessage());
      return null;
    }
    List<ColumnObj> columns = (List<ColumnObj>) columnRepository.findByTableName(schema, table);
    Map<String, String> columnMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    for(ColumnObj col : columns) {
      columnMap.put(col.columnName, col.data_type);
    }
    return columnMap;
  }

  private Connection getConnection() throws SQLException, ResponseStatusException {
    String tenantId = TenantContext.getCurrentTenant();
    Map<String, String> dbMap = dbInfoService.getDBInfo(tenantId);

    if(dbMap == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Unable to get database connection information. Make sure the values are populated");
    }
    
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");
    return dmds.getConnection();
  }
}
