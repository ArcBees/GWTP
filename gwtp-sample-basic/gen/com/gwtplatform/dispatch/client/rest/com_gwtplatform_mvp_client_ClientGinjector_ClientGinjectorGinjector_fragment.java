package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  public void memberInject_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$(com.gwtplatform.dispatch.client.rest.SerializerProvider injectee) {
    
  }
  
  private com.gwtplatform.dispatch.client.rest.SerializerProvider singleton_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$ = null;
  
  public com.gwtplatform.dispatch.client.rest.SerializerProvider get_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$ == null) {
    Object created = GWT.create(com.gwtplatform.dispatch.client.rest.SerializerProvider.class);
    assert created instanceof com.gwtplatform.dispatch.client.rest.SerializerProvider;
    com.gwtplatform.dispatch.client.rest.SerializerProvider result = (com.gwtplatform.dispatch.client.rest.SerializerProvider) created;
    
    memberInject_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$;
    
  }
  
  
  /**
   * Field for the enclosing injector.
   */
  private final com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector;
  public com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment(com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector) {
    this.injector = injector;
  }
  
  public void initializeEagerSingletons() {
    // Eager singleton bound at:
    //   Implicit GWT.create binding for com.gwtplatform.dispatch.client.rest.SerializerProvider
    get_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$();
    
  }
  
}
