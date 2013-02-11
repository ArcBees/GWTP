package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.JsonSerializer;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public class com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer extends JsonSerializer<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> {
  interface Reader extends JsonReader<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> {}
  
  interface Writer extends JsonWriter<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> {}
  
  public com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer() {
    super(GWT.<Reader>create(Reader.class), GWT.<Writer>create(Writer.class));
  }
}
