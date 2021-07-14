package org.folio.ldp;

import java.util.List;

import lombok.ToString;
import lombok.AllArgsConstructor;

@ToString
@AllArgsConstructor
class ColumnFilter {
  public String key;
  public String value;
}

@ToString
@AllArgsConstructor
class OrderingCriterion {
  public String key;
  public String direction;
  public String nulls;
}

@ToString
class TableQuery {
  public String schema;
  public String tableName;
  public List<ColumnFilter> columnFilters;
  public List<String> showColumns;
  public List<OrderingCriterion> orderBy;
  public Integer limit;
}

@ToString
public class QueryObj {
  public List<TableQuery> tables;
}
