package org.folio.ldp;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("unused")
@NoArgsConstructor
@Data
public class ConfigObjId implements Serializable {

  private String key;

  private String tenant;
  
}
