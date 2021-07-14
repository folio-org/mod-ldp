package org.folio.ldp;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ObjectTest {

  @Test
  public void testColumnObj() throws Exception {
    ColumnObj columnObj = new ColumnObj();
    columnObj.setTableName("user_users");
    columnObj.setTableSchema("public");

  }
  
}
