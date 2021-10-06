package org.folio.ldp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.server.ResponseStatusException;

public class ManualLogObjRepository {

  private JdbcTemplate jdbc;

  @Autowired private DBInfoService dbInfoService;

  private void initDB() throws Exception {
    String tenantId = TenantContext.getCurrentTenant();
    Map<String, String> dbMap = dbInfoService.getDBInfo(tenantId);
    if(dbMap == null) {
      throw new Exception("Unable to get db values");
    }
    DriverManagerDataSource dmds = new DriverManagerDataSource(dbMap.get("url"), dbMap.get("user"), dbMap.get("pass"));
    dmds.setDriverClassName("org.postgresql.Driver");

    jdbc = new JdbcTemplate(dmds); 

  }

  public List<LogObj> findAll() {
    //ArrayList<LogObj> logObjList = new ArrayList<>();
    try { 
      initDB();
    } catch(Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Unable to get database connection information. Make sure the values are populated");
    }
    String query = "select * from dbsystem.log";
    return jdbc.query(query, new RowMapper<LogObj>() {
      public LogObj mapRow(ResultSet rs, int rowNumber) throws SQLException {
        LogObj logObj = new LogObj();
        logObj.setLogTime(rs.getTimestamp(1));
        logObj.setTableName(rs.getString(4));
        logObj.setElapsedTime(rs.getString(6));
        logObj.setMessage(rs.getString(5));

        return logObj;
      }
    });


  }
  
}
