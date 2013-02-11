package com.gwtplatform.samples.basic.client.application.response;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy injectee) {
    
  }
  
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.response.ResponsePresenter injectee) {
    injector.getFragment_com_gwtplatform_mvp_client().com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection_(injectee, injector.getFragment_com_gwtplatform_mvp_client().get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    
  }
  
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$Binder$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder injectee) {
    
  }
  
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.response.ResponseView injectee) {
    
  }
  
  
  /**
   * Binding for com.gwtplatform.samples.basic.client.application.response.ResponsePresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  public com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyView get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyView$_annotation$$none$$() {
    com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyView result = get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$();
    return result;
    
  }
  
  private com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$ == null) {
    Object created = GWT.create(com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy.class);
    assert created instanceof com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy;
    com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy result = (com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy) created;
    
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$;
    
  }
  
  private com.gwtplatform.samples.basic.client.application.response.ResponsePresenter singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.client.application.response.ResponsePresenter get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$ == null) {
    com.gwtplatform.samples.basic.client.application.response.ResponsePresenter result = com$gwtplatform$samples$basic$client$application$response$ResponsePresenter_com$gwtplatform$samples$basic$client$application$response$ResponsePresenter_methodInjection(injector.getFragment_com_google_web_bindery_event_shared().get_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyView$_annotation$$none$$(), get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$(), injector.getFragment_com_gwtplatform_mvp_client_proxy().get_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$(), injector.getFragment_com_gwtplatform_dispatch_client_gin().get_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$(), injector.getFragment_com_gwtplatform_samples_basic_shared_dispatch().get_Key$type$com$gwtplatform$samples$basic$shared$dispatch$TextService$_annotation$$none$$());
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$;
    
  }
  
  public com.gwtplatform.samples.basic.client.application.response.ResponsePresenter com$gwtplatform$samples$basic$client$application$response$ResponsePresenter_com$gwtplatform$samples$basic$client$application$response$ResponsePresenter_methodInjection(com.google.web.bindery.event.shared.EventBus _0, com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyView _1, com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.MyProxy _2, com.gwtplatform.mvp.client.proxy.PlaceManager _3, com.gwtplatform.dispatch.shared.DispatchAsync _4, com.gwtplatform.samples.basic.shared.dispatch.TextService _5) {
    return new com.gwtplatform.samples.basic.client.application.response.ResponsePresenter(_0, _1, _2, _3, _4, _5);
  }
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter> declared at:
   *   Implicit injection of com.google.gwt.inject.client.AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter>
   */
  public com.google.gwt.inject.client.AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter> get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$$_annotation$$none$$() {
    com.google.gwt.inject.client.AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter> result = new com.google.gwt.inject.client.AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter>() { 
        public void get(final com.google.gwt.user.client.rpc.AsyncCallback<? super com.gwtplatform.samples.basic.client.application.response.ResponsePresenter> callback) { 
          com.google.gwt.core.client.GWT.runAsync(com.gwtplatform.samples.basic.client.application.response.ResponsePresenter.class,new com.google.gwt.core.client.RunAsyncCallback() { 
            public void onSuccess() { 
              callback.onSuccess(get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$_annotation$$none$$());
            }
            public void onFailure(Throwable ex) { 
               callback.onFailure(ex); 
            } 
        }); 
        }
     };
    
    return result;
    
  }
  
  
  /**
   * Binding for com.gwtplatform.samples.basic.client.application.response.ResponseView$Binder declared at:
   *   Implicit GWT.create binding for com.gwtplatform.samples.basic.client.application.response.ResponseView$Binder
   */
  public com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$Binder$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder.class);
    assert created instanceof com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder;
    com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder result = (com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder) created;
    
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$Binder$_annotation$$none$$(result);
    
    return result;
    
  }
  
  private com.gwtplatform.samples.basic.client.application.response.ResponseView singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.client.application.response.ResponseView get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$ == null) {
    com.gwtplatform.samples.basic.client.application.response.ResponseView result = com$gwtplatform$samples$basic$client$application$response$ResponseView_com$gwtplatform$samples$basic$client$application$response$ResponseView_methodInjection(get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$Binder$_annotation$$none$$());
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponseView$_annotation$$none$$;
    
  }
  
  public com.gwtplatform.samples.basic.client.application.response.ResponseView com$gwtplatform$samples$basic$client$application$response$ResponseView_com$gwtplatform$samples$basic$client$application$response$ResponseView_methodInjection(com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder _0) {
    return new com.gwtplatform.samples.basic.client.application.response.ResponseView(_0);
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
    //   Implicit GWT.create binding for com.gwtplatform.samples.basic.client.application.response.ResponsePresenter$MyProxy
    get_Key$type$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$MyProxy$_annotation$$none$$();
    
  }
  
}
