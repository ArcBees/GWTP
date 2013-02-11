package com.gwtplatform.dispatch.client.gin;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  
  /**
   * Binding for java.lang.String declared at:
   *   protected java.lang.String com.gwtplatform.dispatch.client.gin.RestDispatchAsyncModule.provideRestApplicationPath()
   */
  public java.lang.String get_Key$type$java$lang$String$_annotation$$com$gwtplatform$dispatch$client$rest$RestApplicationPath$() {
    java.lang.String result = com$gwtplatform$dispatch$client$gin$RestDispatchAsyncModule_provideRestApplicationPath_methodInjection(new com.gwtplatform.dispatch.client.gin.RestDispatchAsyncModule());
    return result;
    
  }
  
  public java.lang.String com$gwtplatform$dispatch$client$gin$RestDispatchAsyncModule_provideRestApplicationPath_methodInjection(com.gwtplatform.dispatch.client.gin.RestDispatchAsyncModule invokee) {
    return invokee.provideRestApplicationPath();
  }
  
  private com.gwtplatform.dispatch.shared.DispatchAsync singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$ = null;
  
  public com.gwtplatform.dispatch.shared.DispatchAsync get_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$ == null) {
    com.gwtplatform.dispatch.shared.DispatchAsync result = com$gwtplatform$dispatch$client$gin$RestDispatchAsyncModule_provideDispatchAsync_methodInjection(new com.gwtplatform.dispatch.client.gin.RestDispatchAsyncModule(), injector.getFragment_com_gwtplatform_dispatch_client_rest().get_Key$type$com$gwtplatform$dispatch$client$rest$SerializerProvider$_annotation$$none$$(), get_Key$type$java$lang$String$_annotation$$com$gwtplatform$dispatch$client$rest$RestApplicationPath$());
        singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$;
    
  }
  
  public com.gwtplatform.dispatch.shared.DispatchAsync com$gwtplatform$dispatch$client$gin$RestDispatchAsyncModule_provideDispatchAsync_methodInjection(com.gwtplatform.dispatch.client.gin.RestDispatchAsyncModule invokee, com.gwtplatform.dispatch.client.rest.SerializerProvider _0, java.lang.String _1) {
    return invokee.provideDispatchAsync(_0, _1);
  }
  
  
  /**
   * Field for the enclosing injector.
   */
  private final com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector;
  public com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment(com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector) {
    this.injector = injector;
  }
  
}
