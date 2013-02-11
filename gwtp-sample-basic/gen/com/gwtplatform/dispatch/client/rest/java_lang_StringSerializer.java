package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.JsonSerializer;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public class java_lang_StringSerializer extends JsonSerializer<java.lang.String> {
  interface Reader extends JsonReader<java.lang.String> {}
  
  interface Writer extends JsonWriter<java.lang.String> {}
  
  public java_lang_StringSerializer() {
    super(GWT.<Reader>create(Reader.class), GWT.<Writer>create(Writer.class));
  }
}
