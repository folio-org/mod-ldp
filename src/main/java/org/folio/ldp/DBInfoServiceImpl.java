package org.folio.ldp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
//@ConfigurationProperties("dbinfo")
public class DBInfoServiceImpl  implements DBInfoService {

  @Autowired ConfigObjRepository repo;

  //TODO - These need to be changed to instead query the default datasource

  /*
  @Value("${dbinfo.url}")
  String url;
  @Value("${dbinfo.user}")
  String user;
  @Value("${dbinfo.pass}")
  String pass;
  */

  @Override
  public Map<String, String> getDBInfo(String tenantId) {
    ConfigObjId configId = new ConfigObjId();
    configId.setTenant(tenantId);
    configId.setKey("dbinfo");
    Optional<ConfigObj> result = repo.findById(configId);
    if(result.isPresent()) {
      ConfigObj config = result.get();
      HashMap<String, String> dbMap = new HashMap<>();
      try {
        dbMap.put("url", config.getValue().getString("url"));
        dbMap.put("user", config.getValue().getString("user"));
        dbMap.put("pass", config.getValue().getString("pass"));
        return dbMap;
      } catch(Exception e) {
        System.out.println("Unable to get dbinfo for tenant " + tenantId + ": " + e.getLocalizedMessage());
        return null;
      }
    } else {
      System.out.println("Unable to find dbinfo entry for tenant " + tenantId);
      return null;
    }
  }
  
}
