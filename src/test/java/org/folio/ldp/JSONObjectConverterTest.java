package org.folio.ldp;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;

import org.junit.Test;

public class JSONObjectConverterTest {

  @Test 
  public void goodConvertToJson() {
    JSONObjectConverter converter = new JSONObjectConverter();
    String jsonString = "{ \"cow\" : \"moo\" }";
    JSONObject json = converter.convertToEntityAttribute(jsonString);
    assertTrue(json.get("cow").equals("moo"));

  }

  @Test
  public void badConvertToJson() {
    JSONObjectConverter converter = new JSONObjectConverter();
    String jsonString = "{ \"cow\" : \"moo\" ";
    JSONObject json = converter.convertToEntityAttribute(jsonString);
    assertNull(json);
  }

  @Test 
  public void goodConvertToString() {
    JSONObjectConverter converter = new JSONObjectConverter();
    JSONObject json = new JSONObject();
    json.put("cow", "moo");
    String jsonString = converter.convertToDatabaseColumn(json);
    assertTrue(jsonString.length() > 6);

  }

  @Test public void badConvertToString() {
    JSONObjectConverter converter = new JSONObjectConverter();
    JSONObject json = null;
    String jsonString = converter.convertToDatabaseColumn(json);
    assertTrue(jsonString.isEmpty());

  }
}
