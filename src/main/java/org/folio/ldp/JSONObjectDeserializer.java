package org.folio.ldp;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JSONObjectDeserializer extends JsonDeserializer<JSONObject> {

  @Override
  public JSONObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    JSONObject json;
    String input = null;
    try {
      input = p.readValueAsTree().toString();
    } catch(Exception e) {
      throw new IOException(e);
    }
    System.out.println("Attempting to deserialize JSON: " + input);
    try {
      Object obj = JSONValue.parseWithException(input);
      json = (JSONObject) obj;
    } catch(ParseException pe) {
      throw new IOException(pe);
    }
    return json;
  }
  
}