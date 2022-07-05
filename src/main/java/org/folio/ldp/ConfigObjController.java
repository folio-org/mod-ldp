package org.folio.ldp;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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
      Map<String, Object> returnMap = configObjToMap(result.get());
      sanitizeMap(returnMap);
      return returnMap;
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such key " + key);
    }
  }

  @PutMapping(value="/{key}")
  public Map<String, Object> updateByKey(@PathVariable String key, @RequestBody ConfigObjDTO entity) {
    String tenantId = TenantContext.getCurrentTenant();
    if(entity == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Improperly formatted input");
    }
    if(entity.getKey() == null || !entity.getKey().equals(key)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key in request body must match path");
    }
    entity.setKey(key);
    System.out.println("Got update request with tenant " + tenantId + " and key " + key + " and value " + entity.getValue().toString());
    System.out.println("updateByKey called with key " + key + " and tenant " + tenantId);
    ConfigObjId configObjId = new ConfigObjId();
    configObjId.setTenant(tenantId);
    configObjId.setKey(key);
    ConfigObj returnEntity;
    Optional<ConfigObj> result = repo.findById(configObjId);
    if(result.isPresent()) {
      ConfigObj oldEntity = result.get();
      //Don't allow an empty token or empty password in dbinfo or sqconfig respectively to overwrite an existing value
      if(key != null && (key.equals("dbinfo") || key.equals("sqconfig"))) {
        JSONObject valueJson = entity.getValue();
        JSONObject oldValueJson = oldEntity.getValue();

        String secretKey = "";
        if(key.equals("dbinfo")) { secretKey = "pass"; }
        if(key.equals("sqconfig")) { secretKey = "token"; }
        if( (valueJson.get(secretKey) == null) || ((String)valueJson.get(secretKey)).equals("")) {
          //Use the old stored secretKey
          valueJson.put(secretKey, (String)oldValueJson.get(secretKey));
        }
        oldEntity.setValue(valueJson);
      } else { 
        oldEntity.setValue(entity.getValue());
      }
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
    HashMap<String, Object> jsonMap = new HashMap<>();
    if(config != null) {
      jsonMap.put("tenant", config.getTenant());
      jsonMap.put("key", config.getKey());
      jsonMap.put("value", config.getValue().toString());
    }
    return jsonMap;
  }

  private void sanitizeMap(Map<String, Object> map) {
    if( map == null) {
      return;
    }
    if(map.containsKey("key") && map.get("key").equals("dbinfo")) {
      String jsonValue = (String)map.get("value");
      try {
        JSONObject json = (JSONObject)JSONValue.parseWithException(jsonValue);
        json.put("pass", "");
        map.put("value", json.toJSONString());
      } catch(Exception e) {
        System.out.println("Unable to sanitize dbinfo: " + e.getLocalizedMessage());
      }
    }
    if(map.containsKey("key") && map.get("key").equals("sqconfig")) {
      String jsonValue = (String)map.get("value");
      try {
        JSONObject json = (JSONObject)JSONValue.parseWithException(jsonValue);
        json.put("token", "");
        map.put("value", json.toJSONString());
      } catch(Exception e) {
        System.out.println("Unable to sanitize sqconfig: " + e.getLocalizedMessage());
      }
    }
  }
  
}

