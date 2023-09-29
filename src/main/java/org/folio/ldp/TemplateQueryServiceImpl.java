package org.folio.ldp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.sql.ResultSet;

@Service
public class TemplateQueryServiceImpl implements TemplateQueryService {
  @Override
  public String fetchRemoteSQL(String url) throws IOException {
    URL connectionUrl = new URL(url);
    URLConnection connection = connectionUrl.openConnection();
    InputStream input = connection.getInputStream();
    String encoding = connection.getContentEncoding();
    //default to utf-8 encoding
    encoding = encoding == null ? "UTF-8" : encoding;
    String content = new BufferedReader(new InputStreamReader(input, encoding))
        .lines()
        .collect(Collectors.joining("\n"));

    return content;

  }

  @Override
  public void initializeSQLTemplateFunction(String sql, JdbcTemplate jdbcTemplate) {
    jdbcTemplate.execute(sql);
  }

  @Override
  public String getSQLFunctionName(String sql) {
    String patternString = "--.+:function\\s+(.+)";
    Pattern pattern = Pattern.compile(patternString);

    try {
      Scanner scanner = new Scanner(sql);
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
          return matcher.group(1);
        }
      }
    } catch (Exception e) {
      System.out.println("Error getting function name from sql: " + e.getLocalizedMessage());
    }
    return null;
  }

  @Override
  public Map<String, Object> executeSQLTemplateFunction(String functionName,
      Map<String, String> parameters, Integer limit, JdbcTemplate jdbcTemplate) {
    String functionCall = buildSQLFunctionCall(functionName, parameters);
    String limitClause = limit != null ? " LIMIT " + limit.toString() : "";
    String sql = "SELECT * FROM " + functionCall + limitClause + ";";
    System.out.println("Using sql: " + sql);
    List<Map<String, Object>> content;

    content = jdbcTemplate.query(sql,
        new ResultSetExtractor<List<Map<String,Object>>>() {
      public List<Map<String,Object>> extractData(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (rs.next()) {
          LinkedHashMap<String, Object> row = new LinkedHashMap<>();
          for (int i = 1; i <= columnCount; i++) {
            // Note that the index is 1-based
            String colName = rsmd.getColumnName(i);

            if(colName.equals("data")) { continue; }

            Object colVal = rs.getObject(i);
            row.put(colName, colVal);
          }
          rows.add(row);
        }
        return rows;
      }
    });
    Map<String, Object> value = new HashMap<>();
    value.put("records", content);
    value.put("totalRecords", content != null ? content.size() : 0);
    return value;
  }

  protected String buildSQLFunctionCall(String functionName, Map<String, String> parameters) {
    List<String> stringList = new ArrayList<>();
    for (Map.Entry<String,String> entry : parameters.entrySet()) {
      stringList.add(entry.getKey() + " => '" + entry.getValue() + "'");
    }
    String parmString = String.join(",", stringList);
    return functionName + "(" + parmString + ")";
  }
}
