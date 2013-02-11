package com.gwtplatform.samples.basic.client.dispatch;

import com.gwtplatform.dispatch.client.rest.AbstractRestAction;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;

public class TextService_GetTexts_ActionImpl extends AbstractRestAction<com.gwtplatform.samples.basic.shared.dispatch.BigResult> {
  public TextService_GetTexts_ActionImpl() {
    super(HttpMethod.GET, "/text");
    
  }
  
}
