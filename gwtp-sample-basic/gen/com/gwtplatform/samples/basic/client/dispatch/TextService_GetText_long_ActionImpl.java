package com.gwtplatform.samples.basic.client.dispatch;

import com.gwtplatform.dispatch.client.rest.AbstractRestAction;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;

public class TextService_GetText_long_ActionImpl extends AbstractRestAction<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> {
  TextService_GetText_long_ActionImpl() { /* For serialization */ }
  
  public TextService_GetText_long_ActionImpl(java.lang.Long id) {
    super(HttpMethod.GET, "/text/{uid}");
    
    addPathParam("uid", id);
    
  }
  
}
