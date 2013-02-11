package com.google.web.bindery.event.shared;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  public void memberInject_Key$type$com$google$web$bindery$event$shared$SimpleEventBus$_annotation$$none$$(com.google.web.bindery.event.shared.SimpleEventBus injectee) {
    
  }
  
  private com.google.web.bindery.event.shared.EventBus singleton_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$ = null;
  
  public com.google.web.bindery.event.shared.EventBus get_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$() {
    
    if (singleton_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$ == null) {
    com.google.web.bindery.event.shared.EventBus result = get_Key$type$com$google$web$bindery$event$shared$SimpleEventBus$_annotation$$none$$();
        singleton_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$;
    
  }
  
  
  /**
   * Binding for com.google.web.bindery.event.shared.SimpleEventBus declared at:
   *   Implicit GWT.create binding for com.google.web.bindery.event.shared.SimpleEventBus
   */
  public com.google.web.bindery.event.shared.SimpleEventBus get_Key$type$com$google$web$bindery$event$shared$SimpleEventBus$_annotation$$none$$() {
    Object created = GWT.create(com.google.web.bindery.event.shared.SimpleEventBus.class);
    assert created instanceof com.google.web.bindery.event.shared.SimpleEventBus;
    com.google.web.bindery.event.shared.SimpleEventBus result = (com.google.web.bindery.event.shared.SimpleEventBus) created;
    
    memberInject_Key$type$com$google$web$bindery$event$shared$SimpleEventBus$_annotation$$none$$(result);
    
    return result;
    
  }
  
  
  /**
   * Field for the enclosing injector.
   */
  private final com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector;
  public com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment(com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector) {
    this.injector = injector;
  }
  
}
