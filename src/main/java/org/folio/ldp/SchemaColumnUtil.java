package org.folio.ldp;

import schemacrawler.tools.utility.SchemaCrawlerUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Column;

import java.util.List;

public class SchemaColumnUtil {

  public static List<Column> getColumnsByTableName(Connection conn, String schemaName, String tableName) {
    final Catalog catalog;
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.builder().toOptions();
    catalog = SchemaCrawlerUtility.getCatalog(conn, options);
    
  }
  
}
