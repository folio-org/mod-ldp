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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/ldp/config")
public class ConfigObjController {

  @Autowired ConfigObjRepository repo;

  

  @GetMapping
  public List<ConfigObj> getConfigObjs() {
    String tenantId = TenantContext.getCurrentTenant();
    return (List<ConfigObj>) repo.findByTenant(tenantId);
  }

  @GetMapping(value = "/{key}", produces = "application/json")
  public ConfigObj findByKey(@PathVariable String key) {
    String tenantId = TenantContext.getCurrentTenant();
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant(tenantId);
    configObjId.setKey(key);
    Optional<ConfigObj> result = repo.findById(configObjId);
    if(result.isPresent()) {
      return result.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such key " + key);
    }
  }

  @PutMapping(value="/{key}")
  public ConfigObj updateByKey(@PathVariable String key, @RequestBody ConfigObj entity) {
    String tenantId = TenantContext.getCurrentTenant();
    System.out.println("updateByKey called with key " + key + " and tenant " + tenantId);
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant(tenantId);
    configObjId.setKey(key);
    Optional<ConfigObj> result = repo.findById(configObjId);
    if(result.isPresent()) {
      ConfigObj oldEntity = result.get();
      oldEntity.setValue(entity.getValue());
      repo.save(oldEntity);
    } else {
      entity.setTenant(tenantId);
      repo.save(entity);
    }
    return entity;
  }
  
}
