package org.folio.ldp;

import java.util.List;

import lombok.ToString;

@ToString
class ColumnFilter {
  public String key;
  public String value;
}

@ToString
class TableQuery {
    public String tableName;
    public List<ColumnFilter> columns;
}

@ToString
public class QueryObj {
  public List<TableQuery> tables;
}
