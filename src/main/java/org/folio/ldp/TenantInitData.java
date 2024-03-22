package org.folio.ldp;

import jakarta.persistence.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("unused")
@NoArgsConstructor
@Data
public class TenantInitData {
  public String module_to;
  public String module_from;

}
