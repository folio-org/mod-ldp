package org.folio.ldp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;

public class QueryServiceImplTest {

  private TableQuery tq;

  @Before
  public void setUp() {
    tq = new TableQuery();
    tq.schema = "schema";
    tq.tableName = "table";
  }

  @Test
  public void generateQuerySimple() {
    assertThat(generateQuery(tq), is("SELECT * FROM schema.table LIMIT 500"));
  }

  @Test
  public void generateQueryFilter() {
    tq.columnFilters = List.of(new ColumnFilter("col", ">", "val"));
    assertThat(generateQuery(tq), is("SELECT * FROM schema.table WHERE (\"col\" > 'val') LIMIT 500"));
  }

  @Test
  public void generateQueryFilterWithSingleQuotes() {
    tq.columnFilters = List.of(new ColumnFilter("col", ">", "it's cool, it's \"masked\""));
    assertThat(generateQuery(tq), is(
        "SELECT * FROM schema.table WHERE (\"col\" > 'it''s cool, it''s \"masked\"') LIMIT 500"));
  }

  @Test
  public void generateQueryFilterWithDoubleQuotes() {
    tq.columnFilters = List.of(new ColumnFilter("\"foo\"", ">", "val"));
    var e = assertThrows(ResponseStatusException.class, () -> generateQuery(tq));
    assertThat(e.getMessage(), containsString("must not contain quotes"));
  }

  @Test
  public void generateQueryShowColumns() {
    tq.showColumns = List.of("col1", "col2");
    assertThat(generateQuery(tq), is("SELECT \"col1\",\"col2\" FROM schema.table LIMIT 500"));
  }

  @Test
  public void generateQueryShowColumnsWithQuotes() {
    tq.showColumns = List.of("col1", "col2", "foo\"bar");
    var e = assertThrows(ResponseStatusException.class, () -> generateQuery(tq));
    assertThat(e.getMessage(), containsString("must not contain quotes"));
  }

  private static String generateQuery(TableQuery tableQuery) {
    return new QueryServiceImpl().generateQuery(tableQuery);
  }

}
