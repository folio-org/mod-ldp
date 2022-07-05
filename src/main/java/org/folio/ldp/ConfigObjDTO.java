package org.folio.ldp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.json.simple.JSONObject;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings("unused")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ConfigObjDTO {

  private String key;

  private String tenant;

  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject value;  

  public ConfigObjDTO(String key, String tenant, JSONObject value) {
    this.key = key;
    this.tenant = tenant;
    if(value != null) {
      this.value = value;
    } else {
      this.value = new JSONObject();
    }
  }

  public ConfigObjDTO() {
    this.key = null;
    this.tenant = null;
    this.value = new JSONObject();
  }
}
