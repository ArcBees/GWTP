
package com.gwtplatform.dispatch.client.rest;

import static java.util.logging.Level.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;

import name.pehl.piriti.commons.client.InstanceContextHolder;
import name.pehl.piriti.json.client.AbstractJsonReader;
import name.pehl.piriti.json.client.JsonReader;
import name.pehl.totoe.json.client.JsonPath;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class java_lang_StringSerializer_ReaderImpl extends AbstractJsonReader<java.lang.String> implements com.gwtplatform.dispatch.client.rest.java_lang_StringSerializer.Reader 
{
    // ----------------------------------------------------------- constructors

    public java_lang_StringSerializer_ReaderImpl() 
    {
        this.jsonRegistry.register(java.lang.String.class, this);
    }
    
    
    // ------------------------------------------------------------ new methods
    
    protected java.lang.String newModel(JSONObject jsonObject)
    {
        java.lang.String model = null;
                    model = GWT.create(java.lang.String.class); 
                return model;
    }

    // ----------------------------------------- ids, properties and references

    protected java.lang.String readId(JSONObject jsonObject)
    {
        // IDs in JSON are nor supported!
        return newModel(jsonObject);
    }


    protected java.lang.String readProperties(JSONObject jsonObject, java.lang.String model) 
    {
        if (jsonObject != null) 
        {
                    }
        return model;
    }


    protected java.lang.String readIdRefs(JSONObject jsonObject, java.lang.String model)
    {
        // References in JSON are nor supported!
        return model;
    }
}