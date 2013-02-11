package com.gwtplatform.samples.basic.shared.dispatch;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  public void memberInject_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$(com.gwtplatform.samples.basic.shared.dispatch.TextService injectee) {
    
  }
  
  private com.gwtplatform.samples.basic.shared.dispatch.TextService singleton_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.shared.dispatch.TextService get_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$ == null) {
    Object created = GWT.create(com.gwtplatform.samples.basic.shared.dispatch.TextService.class);
    assert created instanceof com.gwtplatform.samples.basic.shared.dispatch.TextService;
    com.gwtplatform.samples.basic.shared.dispatch.TextService result = (com.gwtplatform.samples.basic.shared.dispatch.TextService) created;
    
    memberInject_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$;
    
  }
  
  
  /**
   * Field for the enclosing injector.
   */
  private final com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector;
  public com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment(com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector) {
    this.injector = injector;
  }
  
}
