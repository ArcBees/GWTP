package com.gwtplatform.dispatch.shared.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.JsonSerializer;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;
import java.lang.String;

public class java_lang_String extends JsonSerializer<String> {
    interface Reader extends JsonReader<String> {}

    interface Writer extends JsonWriter<String> {}

    public java_lang_String() {
        super(GWT.<Reader>create(Reader.class), GWT.<Writer>create(Writer.class));
    }
}
