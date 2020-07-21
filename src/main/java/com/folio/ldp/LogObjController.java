package org.folio.ldp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/ldp/db/log")
public class LogObjController {
  @Autowired LogObjRepository logRepository;
  
  @GetMapping
  public List<LogObj> getLogObjs() {
      return (List<LogObj>) logRepository.findAll();
  }
}