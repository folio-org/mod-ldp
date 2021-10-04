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

@RestController
@RequestMapping("/ldp/db/tables")
public class TableObjController {
  //@Autowired TableObjRepository tableRepository;

  CrawlTableObjRepository tableRepository = new CrawlTableObjRepository();
  @Autowired DBInfoService dbInfoService;

  @GetMapping
  public List<TableObj> getTableObjs() {
    try {
      tableRepository.setConnection(getConnection());
    } catch(Exception e) {
      System.out.println("Error getting connection " + e.getLocalizedMessage());
      return null;
    }
    return (List<TableObj>) tableRepository.getAllTablesBySchema();
  }

  @Cacheable(cacheNames="tableMap")
  public Map<String, Boolean> getTablesAsMap() {
    try {
      tableRepository.setConnection(getConnection());
    } catch(Exception e) {
      System.out.println("Error getting connection " + e.getLocalizedMessage());
      return null;
    }
    List<TableObj> tables = (List<TableObj>) tableRepository.findAll();
    Map<String, Boolean> tableMap = new TreeMap<String, Boolean>(String.CASE_INSENSITIVE_ORDER);
    for(TableObj table : tables) {
      tableMap.put(table.tableName, true);
    }
    return tableMap;
  }

  private Connection getConnection() throws SQLException {
    Map<String, String> dbMap = dbInfoService.getDBInfo();
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");
    return dmds.getConnection();
  }
}
