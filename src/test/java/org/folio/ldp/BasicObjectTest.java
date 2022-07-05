package org.folio.ldp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.simple.JSONObject;
import org.junit.Test;

public class BasicObjectTest {

  @Test
  public void testConfigObjDTOConstructors() {
    ConfigObjDTO cod = new ConfigObjDTO("somekey", "sometenant", new JSONObject());
    assertEquals("somekey", cod.getKey());

    ConfigObjDTO cod2 = new ConfigObjDTO();
    assertNull(cod2.getKey());

  }
  
}
