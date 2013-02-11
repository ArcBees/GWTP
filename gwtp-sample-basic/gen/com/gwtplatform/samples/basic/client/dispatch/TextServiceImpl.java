package com.gwtplatform.samples.basic.client.dispatch;

import com.gwtplatform.samples.basic.shared.dispatch.TextService;

public class TextServiceImpl implements TextService {
  @Override
  public com.gwtplatform.dispatch.shared.Action<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> createText(java.lang.String body) {
    return new TextService_CreateText_String_ActionImpl(body);
  }
  
  @Override
  public com.gwtplatform.dispatch.shared.Action<com.gwtplatform.samples.basic.shared.dispatch.BigResult> getTexts() {
    return new TextService_GetTexts_ActionImpl();
  }
  
  @Override
  public com.gwtplatform.dispatch.shared.Action<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> getText(long id) {
    return new TextService_GetText_long_ActionImpl(id);
  }
  
}
