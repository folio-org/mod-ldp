package org.folio.ldp;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.json.JSONObject;


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
  public Map<String, Object> findByKey(@PathVariable String key) {
    String tenantId = TenantContext.getCurrentTenant();
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant(tenantId);
    configObjId.setKey(key);
    Optional<ConfigObj> result = repo.findById(configObjId);
    if(result.isPresent()) {
      return configObjToMap(result.get());
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such key " + key);
    }
  }

  @PutMapping(value="/{key}")
  public Map<String, Object> updateByKey(@PathVariable String key, @RequestBody ConfigObjDTO entity) {
    String tenantId = TenantContext.getCurrentTenant();
    System.out.println("Got update request with tenant " + tenantId + " and key " + key + " and value " + entity.getValue().toString());
    System.out.println("updateByKey called with key " + key + " and tenant " + tenantId);
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant(tenantId);
    configObjId.setKey(key);
    ConfigObj returnEntity;
    Optional<ConfigObj> result = repo.findById(configObjId);
    if(result.isPresent()) {
      ConfigObj oldEntity = result.get();
      oldEntity.setValue(entity.getValue());
      oldEntity.setTenant(tenantId);
      repo.save(oldEntity);
      returnEntity = oldEntity;
    } else {
      ConfigObj saveEntity = new ConfigObj();
      saveEntity.setTenant(tenantId);
      saveEntity.setKey(entity.getKey());
      saveEntity.setValue(entity.getValue());
      
      repo.save(saveEntity);
      returnEntity = saveEntity;
    }
    return configObjToMap(returnEntity);
  }

  private Map<String, Object> configObjToMap(ConfigObj config) {
    HashMap<String, Object> json = new HashMap<>();
    json.put("tenant", config.getTenant());
    json.put("key", config.getKey());
    json.put("value", config.getValue().toString());
    return json;
  }
  
}

