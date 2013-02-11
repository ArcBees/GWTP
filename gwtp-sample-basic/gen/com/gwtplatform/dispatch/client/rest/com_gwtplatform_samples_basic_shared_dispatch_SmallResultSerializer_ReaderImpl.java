
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

public class com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer_ReaderImpl extends AbstractJsonReader<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> implements com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer.Reader 
{
    // ----------------------------------------------------------- constructors

    public com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer_ReaderImpl() 
    {
        this.jsonRegistry.register(com.gwtplatform.samples.basic.shared.dispatch.SmallResult.class, this);
    }
    
    
    // ------------------------------------------------------------ new methods
    
    protected com.gwtplatform.samples.basic.shared.dispatch.SmallResult newModel(JSONObject jsonObject)
    {
        com.gwtplatform.samples.basic.shared.dispatch.SmallResult model = null;
                    model = GWT.create(com.gwtplatform.samples.basic.shared.dispatch.SmallResult.class); 
                return model;
    }

    // ----------------------------------------- ids, properties and references

    protected com.gwtplatform.samples.basic.shared.dispatch.SmallResult readId(JSONObject jsonObject)
    {
        // IDs in JSON are nor supported!
        return newModel(jsonObject);
    }


    protected com.gwtplatform.samples.basic.shared.dispatch.SmallResult readProperties(JSONObject jsonObject, com.gwtplatform.samples.basic.shared.dispatch.SmallResult model) 
    {
        if (jsonObject != null) 
        {
            				
                // parsing template name/pehl/piriti/rebind/json/reader/property/string.vm
				if (logger.isLoggable(FINE)) 
				{
				    logger.log(FINE, "Processing java.lang.String myResult");
				}
				java.lang.String value2 = null;
			    			        JSONValue value2JsonValue = jsonObject.get("myResult");
			    			    if (value2JsonValue != null)
                {
                                            if (value2JsonValue.isNull() == null) 
    {
        JSONString jsonString = value2JsonValue.isString();
        if (jsonString != null)
        {
            value2 = jsonString.stringValue();
                    }
    }
                    	if (value2 != null)
	{
	    	        	            model.setMyResult(value2);
	        	    	}
			    }
                    }
        return model;
    }


    protected com.gwtplatform.samples.basic.shared.dispatch.SmallResult readIdRefs(JSONObject jsonObject, com.gwtplatform.samples.basic.shared.dispatch.SmallResult model)
    {
        // References in JSON are nor supported!
        return model;
    }
}