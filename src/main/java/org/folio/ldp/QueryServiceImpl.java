package org.folio.ldp;

import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.OrderObject.Dir;
import com.healthmarketscience.sqlbuilder.OrderObject.NullOrder;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.custom.mysql.MysLimitClause;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QueryServiceImpl implements QueryService {

  private String quote(String column) {
    // reject embedded quote that leads to SQL injection
    // https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS
    if (column.contains("\"")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Column name must not contain quotes: " + column);
    }
    return "\"" + column + "\"";
  }

  private BinaryCondition makeCond(ColumnFilter filter) {
    // mask single quotes to avoid SQL injection
    // https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-STRINGS
    // https://openhms.sourceforge.io/sqlbuilder/ "Does not do any form of SQL string escaping"
    var val = filter.value.replace("'", "''");

    CustomSql key = new CustomSql(quote(filter.key));
    if (filter.op == null) return BinaryCondition.equalTo(key, val);

    switch (filter.op) {
    case "=":     return BinaryCondition.equalTo(key, val);
    case "<>":    return BinaryCondition.notEqualTo(key, val);
    case "<":     return BinaryCondition.lessThan(key, val);
    case "<=":    return BinaryCondition.lessThanOrEq(key, val);
    case ">":     return BinaryCondition.greaterThan(key, val);
    case ">=":    return BinaryCondition.greaterThanOrEq(key, val);
    case "LIKE":  return BinaryCondition.like(key, val);
    case "ILIKE": return new BinaryCondition(" ILIKE ", key, val);
    default:      return BinaryCondition.equalTo(key, val);
    }
  }

  @Override
  public String generateQuery(TableQuery query) {

    SelectQuery selectQuery = (new SelectQuery())
      .addCustomFromTable(query.schema + '.' + query.tableName);

    if(query.showColumns == null || query.showColumns.isEmpty()) {
      selectQuery.addAllColumns();
    } else {
      for (String col : query.showColumns) {
        selectQuery = selectQuery.addCustomColumns(new CustomSql(quote(col)));
      }
    }

    if(query.columnFilters != null) {
      for (ColumnFilter col : query.columnFilters) {
        if(col == null || col.key == null || col.key.equals("") || col.value == null || col.value.equals("") ) {
          continue;
        }
        selectQuery = selectQuery.addCondition(makeCond(col));
      }
    }

    if (query.orderBy != null) {
      for (OrderingCriterion ord : query.orderBy) {
        if(ord == null || ord.key == null || ord.key.equals("") ) { continue; }
        Dir dir = (ord.direction.equals("desc")) ? Dir.DESCENDING : Dir.ASCENDING;
        NullOrder nulls = (ord.nulls.equals("start")) ? NullOrder.FIRST : NullOrder.LAST;
        OrderObject oo = new OrderObject(dir, quote(ord.key)).setNullOrder(nulls);
        selectQuery = selectQuery.addCustomOrderings(oo);
      }
    }

    if (query.limit != null) {
      selectQuery.addCustomization(new MysLimitClause(query.limit));
    }

    String selectQueryStr = selectQuery.validate().toString();
    System.out.println(selectQueryStr);
    String rawQueryContent = selectQueryStr;

    final String queryContent;
    if (!rawQueryContent.toLowerCase().contains("limit")) {
      queryContent = rawQueryContent + " LIMIT 500";
    } else {
      queryContent = rawQueryContent;
    }

    return queryContent;


  }

}
