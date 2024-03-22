package org.folio.ldp;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import org.folio.ldp.JSONObjectConverter;
import org.springframework.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.json.simple.JSONObject;

@Entity
@IdClass(ConfigObjId.class)
@NoArgsConstructor
@Data
@Table(name = "config")
public class ConfigObj {
  @Id
  private String key;

  @Id
  private String tenant;

  @NonNull
  @Column(columnDefinition = "TEXT")
  @Convert(converter=JSONObjectConverter.class)
  private JSONObject value;
}
