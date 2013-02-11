package com.gwtplatform.dispatch.shared.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.JsonSerializer;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;
import com.gwtplatform.samples.basic.shared.dispatch.SmallResult;

public class com_gwtplatform_samples_basic_shared_dispatch_SmallResult extends JsonSerializer<SmallResult> {
    interface Reader extends JsonReader<SmallResult> {}

    interface Writer extends JsonWriter<SmallResult> {}

    public com_gwtplatform_samples_basic_shared_dispatch_SmallResult() {
        super(GWT.<Reader>create(Reader.class), GWT.<Writer>create(Writer.class));
    }
}
