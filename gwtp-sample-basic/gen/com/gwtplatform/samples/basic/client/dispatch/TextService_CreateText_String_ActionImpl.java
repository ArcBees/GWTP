package com.gwtplatform.samples.basic.client.dispatch;

import com.gwtplatform.dispatch.client.rest.AbstractRestAction;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;

public class TextService_CreateText_String_ActionImpl extends AbstractRestAction<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> {
  TextService_CreateText_String_ActionImpl() { /* For serialization */ }
  
  public TextService_CreateText_String_ActionImpl(java.lang.String body) {
    super(HttpMethod.POST, "/text");
    
    setBodyParam(body);
  }
  
}
