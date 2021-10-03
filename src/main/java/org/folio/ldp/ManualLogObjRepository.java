package org.folio.ldp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ManualLogObjRepository {

  private JdbcTemplate jdbc;

  public void initDB(String url, String user, String pass, String driver) {
    DriverManagerDataSource dmds = new DriverManagerDataSource();
    dmds.setDriverClassName(driver);
    dmds.setUrl(url);
    dmds.setUsername(user);
    dmds.setPassword(pass);

    jdbc = new JdbcTemplate(dmds); 

  }

  public List<LogObj> getLogObjs() {
    //ArrayList<LogObj> logObjList = new ArrayList<>();
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
