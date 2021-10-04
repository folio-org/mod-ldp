package org.folio.ldp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
//@ConfigurationProperties("dbinfo")
public class DBInfoServiceImpl  implements DBInfoService {

  @Value("${dbinfo.url}")
  String url;
  @Value("${dbinfo.user}")
  String user;
  @Value("${dbinfo.pass}")
  String pass;

  @Override
  public Map<String, String> getDBInfo() {
    HashMap<String, String> dbMap = new HashMap<>();
    dbMap.put("url", url);
    dbMap.put("user", user);
    dbMap.put("pass", pass);
    return dbMap;
  }
  
}
