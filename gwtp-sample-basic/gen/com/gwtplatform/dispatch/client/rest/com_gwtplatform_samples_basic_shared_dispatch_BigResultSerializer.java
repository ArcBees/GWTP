package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.JsonSerializer;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public class com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer extends JsonSerializer<com.gwtplatform.samples.basic.shared.dispatch.BigResult> {
  interface Reader extends JsonReader<com.gwtplatform.samples.basic.shared.dispatch.BigResult> {}
  
  interface Writer extends JsonWriter<com.gwtplatform.samples.basic.shared.dispatch.BigResult> {}
  
  public com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer() {
    super(GWT.<Reader>create(Reader.class), GWT.<Writer>create(Writer.class));
  }
}
