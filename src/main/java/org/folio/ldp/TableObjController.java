package org.folio.ldp;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ldp/db/tables")
public class TableObjController {
  @Autowired TableObjRepository tableRepository;

  @GetMapping
  public List<TableObj> getTableObjs() {
    return (List<TableObj>) tableRepository.getAllTablesBySchema();
  }

  @Cacheable(cacheNames="tableMap")
  public Map<String, Boolean> getTablesAsMap() {
    List<TableObj> tables = (List<TableObj>) tableRepository.findAll();
    Map<String, Boolean> tableMap = new TreeMap<String, Boolean>(String.CASE_INSENSITIVE_ORDER);
    for(TableObj table : tables) {
      tableMap.put(table.tableName, true);
    }
    return tableMap;
  }
}
