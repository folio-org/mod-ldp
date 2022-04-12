package org.folio.ldp;

import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.OrderObject.Dir;
import com.healthmarketscience.sqlbuilder.OrderObject.NullOrder;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.custom.mysql.MysLimitClause;

import org.springframework.stereotype.Service;

@Service
public class QueryServiceImpl implements QueryService {

  private String quote(String s) {
    return "\"" + s + "\"";
  }

  private BinaryCondition makeCond(ColumnFilter filter) {
    CustomSql key = new CustomSql(quote(filter.key));
    if (filter.op == null) return BinaryCondition.equalTo(key, filter.value);

    switch (filter.op) {
    case "=":     return BinaryCondition.equalTo(key, filter.value);
    case "<>":    return BinaryCondition.notEqualTo(key, filter.value);
    case "<":     return BinaryCondition.lessThan(key, filter.value);
    case "<=":    return BinaryCondition.lessThanOrEq(key, filter.value);
    case ">":     return BinaryCondition.greaterThan(key, filter.value);
    case ">=":    return BinaryCondition.greaterThanOrEq(key, filter.value);
    case "LIKE":  return BinaryCondition.like(key, filter.value);
    case "ILIKE": return BinaryCondition.like(key, filter.value); // XXX should be ILIKE
    default:      return BinaryCondition.equalTo(key, filter.value);
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
