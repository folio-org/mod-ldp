package org.folio.ldp;

import schemacrawler.tools.utility.SchemaCrawlerUtility;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Column;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;

public class SchemaColumnUtil {

  public static List<Column> getColumnsByTableName(Connection conn, String schemaName,
   String tableName) throws SchemaCrawlerException {
    final Catalog catalog;
    final ArrayList<Column> columnList = new ArrayList<>();
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
   
    catalog = SchemaCrawlerUtility.getCatalog(conn, options);
    for( final Schema schema : catalog.getSchemas()) {
      System.out.println("Schema: " + schema.getName());
      if(!schema.getName().equals(schemaName)) {
        continue;
      } else {
        for( final Table table : catalog.getTables(schema)) {
          System.out.println("Table: " + table.getName() + " has " + 
            table.getColumns().size() + " columns");
          if(!table.getName().equals(tableName)) {
            continue;
          } else {
            for( final Column column : table.getColumns()) {
              System.out.println("Column: " + column.getName());
              columnList.add(column);
            }
          }
        }
      }
    }
    
    return columnList;
  }
  
}
