
package com.gwtplatform.dispatch.client.rest;

import static java.util.logging.Level.*;

import java.util.Iterator;

import name.pehl.piriti.commons.client.ModelWriteEvent;
import name.pehl.piriti.json.client.AbstractJsonWriter;
import name.pehl.piriti.json.client.JsonWriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;


public class java_lang_StringSerializer_WriterImpl extends AbstractJsonWriter<java.lang.String> implements com.gwtplatform.dispatch.client.rest.java_lang_StringSerializer.Writer 
{
    // ----------------------------------------------------------- constructors

    public java_lang_StringSerializer_WriterImpl() 
    {
        this.jsonRegistry.register(java.lang.String.class, this);
    }
    
    
    // ---------------------------------------------------------- write methods

    @Override
    public String toJson(java.lang.String model)
    {
        String json = null;
        if (model != null) 
        {
            StringBuilder out = new StringBuilder();
            out.append("{");
                        out.append("}");
            json = out.toString();
            ModelWriteEvent.fire(this, model, json);
        }
        return json;
    }
}