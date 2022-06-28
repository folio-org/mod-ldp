package org.folio.ldp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

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
