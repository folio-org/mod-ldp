package org.folio.ldp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ldp/config")
public class ConfigObjController {

  @Autowired ConfigObjRepository repo;

  @GetMapping
  public List<ConfigObj> getConfigObjs() {
    return (List<ConfigObj>) repo.findAll();
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  public ConfigObj findByKey(@PathVariable String key) {
    Optional<ConfigObj> result = repo.findById(id);
    if(result.isPresent()) {
      return result.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND. "No such key " + key);
    }
  }
  
}
