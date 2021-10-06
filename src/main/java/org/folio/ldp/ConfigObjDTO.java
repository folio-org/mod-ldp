package org.folio.ldp;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@SuppressWarnings("unused")
@NoArgsConstructor
@Data
public class ConfigObjDTO {

  private String key;
  private String tenant;
  private JSONObject value;  
}
