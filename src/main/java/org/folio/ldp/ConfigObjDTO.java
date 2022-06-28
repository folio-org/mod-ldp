package org.folio.ldp;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings("unused")
@NoArgsConstructor
@Data
public class ConfigObjDTO {

  private String key;
  private String tenant;

  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject value;  
}
