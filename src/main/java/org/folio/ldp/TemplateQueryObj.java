package org.folio.ldp;

import lombok.ToString;

import java.util.Map;

@ToString
public class TemplateQueryObj {
  public String url;
  public Map<String, String> params;
}

