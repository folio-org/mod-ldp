package org.folio.ldp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@RestController
@RequestMapping(value = "/ldp/db/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateQueryController {

  @Autowired
  private DBInfoService dbInfoService;

  @Autowired
  private TemplateQueryService templateQueryService;

  private JdbcTemplate jdbc;

  @PostMapping
  public Map<String, Object> postTemplateQuery(@RequestBody TemplateQueryObj templateQueryObj,
      HttpServletResponse response) {
    Map<String, Object> content;
    String tenantId = TenantContext.getCurrentTenant();

    Map<String, String> dbMap = dbInfoService.getDBInfo(tenantId);

    if (dbMap == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Error: Unable to get database connection information. Make sure the values are populated");
    }

    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"),
        dbMap.get("pass"));

    dmds.setDriverClassName("org.postgresql.Driver");

    jdbc = new JdbcTemplate(dmds);

    //We need to use a transaction manager to rollback our queries after using them
    final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dmds);

    TransactionStatus transactionStatus
        = transactionManager.getTransaction(new DefaultTransactionDefinition());
    transactionStatus.setRollbackOnly();
    try {
      String templateSQL = templateQueryService.fetchRemoteSQL(templateQueryObj.url);
      String functionName = templateQueryService.getSQLFunctionName(templateSQL);
      templateQueryService.initializeSQLTemplateFunction(templateSQL, jdbc);
      content = templateQueryService.executeSQLTemplateFunction(functionName, templateQueryObj.params,
         jdbc);
    } catch(Exception e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Error generating report " + templateQueryObj.url +
          " : " + e.getLocalizedMessage());
    } finally {
      // Always rollback
      if (!transactionStatus.isCompleted()) {
        transactionManager.rollback(transactionStatus);
      }
    }
    return content;
  }

}



