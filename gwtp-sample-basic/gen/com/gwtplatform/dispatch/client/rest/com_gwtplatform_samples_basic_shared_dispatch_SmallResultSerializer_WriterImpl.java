
package com.gwtplatform.dispatch.client.rest;

import static java.util.logging.Level.*;

import java.util.Iterator;

import name.pehl.piriti.commons.client.ModelWriteEvent;
import name.pehl.piriti.json.client.AbstractJsonWriter;
import name.pehl.piriti.json.client.JsonWriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;


public class com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer_WriterImpl extends AbstractJsonWriter<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> implements com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer.Writer 
{
    // ----------------------------------------------------------- constructors

    public com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer_WriterImpl() 
    {
        this.jsonRegistry.register(com.gwtplatform.samples.basic.shared.dispatch.SmallResult.class, this);
    }
    
    
    // ---------------------------------------------------------- write methods

    @Override
    public String toJson(com.gwtplatform.samples.basic.shared.dispatch.SmallResult model)
    {
        String json = null;
        if (model != null) 
        {
            StringBuilder out = new StringBuilder();
            out.append("{");
            				
                // parsing template name/pehl/piriti/rebind/json/writer/property/string.vm
                if (logger.isLoggable(FINE)) 
                {
                    logger.log(FINE, "Processing java.lang.String myResult");
                }
				java.lang.String value3 = null;
			    			        			            value3 = model.getMyResult(); 
			        			    				out.append(JsonUtils.escapeValue("myResult")).append(":");
                                if (value3 != null)
{
    	out.append(JsonUtils.escapeValue(value3));
}
else
{
    out.append("null");
}
	                                    out.append("}");
            json = out.toString();
            ModelWriteEvent.fire(this, model, json);
        }
        return json;
    }
}