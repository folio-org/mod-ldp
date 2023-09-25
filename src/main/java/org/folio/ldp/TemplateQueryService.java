package org.folio.ldp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TemplateQueryService {
  public abstract String fetchRemoteSQL(String url) throws IOException;
  public abstract void initializeSQLTemplateFunction(String sql,
       JdbcTemplate jdbcTemplate);
  public abstract String getSQLFunctionName(String sql);
  public abstract List<Map<String,Object>> executeSQLTemplateFunction(String functionName,
      Map<String, String> parameters, JdbcTemplate jdbcTemplate);
}
