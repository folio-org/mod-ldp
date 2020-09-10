package org.folio.ldp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/ldp/db/query", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryController {
  @Autowired
  private JdbcTemplate jdbc;

  @GetMapping
  public List<Map<String, Object>> getQuery(String table) {

    // TODO: Validate table string
    
    final String rawQueryContent = "SELECT * FROM public."+table; // query.getContent().trim();
    final String queryContent;
    if (!rawQueryContent.toLowerCase().contains("limit")) {
      queryContent = rawQueryContent + " LIMIT 500";
    } else {
      queryContent = rawQueryContent;
    }
    List<Map<String, Object>> content = jdbc.query(queryContent, new ResultSetExtractor<List<Map<String, Object>>>() {
      public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {

          List<Map<String, Object>> rows = new ArrayList<>();
          ResultSetMetaData rsmd = rs.getMetaData();
          int columnCount = rsmd.getColumnCount();
          
          while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
              // Note that the index is 1-based
              String colName = rsmd.getColumnName(i);
              Object colVal = rs.getObject(i);
              row.put(colName, colVal);
            }
            rows.add(row);
          }
          return rows;
        }      
    });
    return content;
  }
}
