package org.folio.ldp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/ldp/db/columns")
public class ColumnObjController {
  @Autowired ColumnObjRepository columnRepository;
  
  @GetMapping
  public List<ColumnObj> getColumnsForTable(String table) {

    // TODO: Validate table string

    return (List<ColumnObj>) columnRepository.findByTableName(table);
  }
}
