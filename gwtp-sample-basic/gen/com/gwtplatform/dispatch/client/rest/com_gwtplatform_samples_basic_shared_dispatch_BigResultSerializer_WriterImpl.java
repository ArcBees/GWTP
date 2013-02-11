
package com.gwtplatform.dispatch.client.rest;

import static java.util.logging.Level.*;

import java.util.Iterator;

import name.pehl.piriti.commons.client.ModelWriteEvent;
import name.pehl.piriti.json.client.AbstractJsonWriter;
import name.pehl.piriti.json.client.JsonWriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;


public class com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer_WriterImpl extends AbstractJsonWriter<com.gwtplatform.samples.basic.shared.dispatch.BigResult> implements com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer.Writer 
{
    // ----------------------------------------------------------- constructors

    public com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer_WriterImpl() 
    {
        this.jsonRegistry.register(com.gwtplatform.samples.basic.shared.dispatch.BigResult.class, this);
    }
    
    
    // ---------------------------------------------------------- write methods

    @Override
    public String toJson(com.gwtplatform.samples.basic.shared.dispatch.BigResult model)
    {
        String json = null;
        if (model != null) 
        {
            StringBuilder out = new StringBuilder();
            out.append("{");
            				
                // parsing template name/pehl/piriti/rebind/json/writer/property/collection.vm
                if (logger.isLoggable(FINE)) 
                {
                    logger.log(FINE, "Processing java.util.List<java.lang.String> myResults");
                }
				java.util.List<java.lang.String> value1 = null;
			    			        			            value1 = model.getMyResults(); 
			        			    				out.append(JsonUtils.escapeValue("myResults")).append(":");
                                	if (value1 != null)
{
        out.append("[");
    for (Iterator<java.lang.String> iter = value1.iterator(); iter.hasNext(); )
    {
        boolean elementWritten = true;
        java.lang.String currentValue = iter.next();
        if (currentValue != null)
        {
            out.append(JsonUtils.escapeValue(currentValue));
        }
        else
        {
            out.append("null");
        }
        if (iter.hasNext() && elementWritten)
        {
            out.append(",");
        }
    }
    out.append("]");
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