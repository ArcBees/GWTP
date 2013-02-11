package com.gwtplatform.dispatch.shared.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.JsonSerializer;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;
import com.gwtplatform.samples.basic.shared.dispatch.BigResult;

public class com_gwtplatform_samples_basic_shared_dispatch_BigResult extends JsonSerializer<BigResult> {
    interface Reader extends JsonReader<BigResult> {}

    interface Writer extends JsonWriter<BigResult> {}

    public com_gwtplatform_samples_basic_shared_dispatch_BigResult() {
        super(GWT.<Reader>create(Reader.class), GWT.<Writer>create(Writer.class));
    }
}
