
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

public class com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer_ReaderImpl extends AbstractJsonReader<com.gwtplatform.samples.basic.shared.dispatch.BigResult> implements com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer.Reader 
{
    // ----------------------------------------------------------- constructors

    public com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer_ReaderImpl() 
    {
        this.jsonRegistry.register(com.gwtplatform.samples.basic.shared.dispatch.BigResult.class, this);
    }
    
    
    // ------------------------------------------------------------ new methods
    
    protected com.gwtplatform.samples.basic.shared.dispatch.BigResult newModel(JSONObject jsonObject)
    {
        com.gwtplatform.samples.basic.shared.dispatch.BigResult model = null;
                    model = GWT.create(com.gwtplatform.samples.basic.shared.dispatch.BigResult.class); 
                return model;
    }

    // ----------------------------------------- ids, properties and references

    protected com.gwtplatform.samples.basic.shared.dispatch.BigResult readId(JSONObject jsonObject)
    {
        // IDs in JSON are nor supported!
        return newModel(jsonObject);
    }


    protected com.gwtplatform.samples.basic.shared.dispatch.BigResult readProperties(JSONObject jsonObject, com.gwtplatform.samples.basic.shared.dispatch.BigResult model) 
    {
        if (jsonObject != null) 
        {
            				
                // parsing template name/pehl/piriti/rebind/json/reader/property/collection.vm
				if (logger.isLoggable(FINE)) 
				{
				    logger.log(FINE, "Processing java.util.List<java.lang.String> myResults");
				}
				java.util.List<java.lang.String> value0 = null;
			    			        JSONValue value0JsonValue = jsonObject.get("myResults");
			    			    if (value0JsonValue != null)
                {
                                        	JSONArray jsonArray = value0JsonValue.isArray();
if (jsonArray != null)
{
    int size = jsonArray.size();
    value0 = new java.util.ArrayList<java.lang.String>();
        for (int i = 0; i < size; i++)
    {
        JSONValue currentJsonValue = jsonArray.get(i);
        if (currentJsonValue != null && currentJsonValue.isNull() == null) 
        {
            java.lang.String currentValue = null;
            JSONString jsonString = currentJsonValue.isString();
if (jsonString != null)
{
    currentValue = jsonString.stringValue();
    }
            if (currentValue != null) 
            {
                value0.add(currentValue);
            }
        }
    }
}
                    	if (value0 != null)
	{
	    	        	            model.setMyResults(value0);
	        	    	}
			    }
                    }
        return model;
    }


    protected com.gwtplatform.samples.basic.shared.dispatch.BigResult readIdRefs(JSONObject jsonObject, com.gwtplatform.samples.basic.shared.dispatch.BigResult model)
    {
        // References in JSON are nor supported!
        return model;
    }
}