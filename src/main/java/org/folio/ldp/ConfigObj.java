package org.folio.ldp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.folio.ldp.JSONObjectConverter;
import org.json.JSONObject;
import org.springframework.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "config")
public class ConfigObj {
  @Id
  @NonNull
  private String key;

  @NonNull
  @Column(columnDefinition = "TEXT")
  @Convert(converter=JSONObjectConverter.class)
  private JSONObject value;
}
