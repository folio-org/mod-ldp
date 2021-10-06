/* Converter class to convert our JSONObject fields in entities to strings and back */

package org.folio.ldp;

import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {

  @Override
  public String convertToDatabaseColumn(JSONObject json) {
    String jsonString;
    try {
      jsonString = json.toString();
    } catch(NullPointerException npe) {
      jsonString = "";
    }
    return jsonString;
  }

  @Override
  public JSONObject convertToEntityAttribute(String string) {
    JSONObject json;
    try {
      json = new JSONObject(string);
    } catch(JSONException je) {
      System.out.println("Unable to convert string " + string + " to JSONObject: " + je.getLocalizedMessage());
      json = null;
    }
    return json;
  }
  
}