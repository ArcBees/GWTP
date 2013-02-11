package com.gwtplatform.samples.basic.client.application;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy injectee) {
    
  }
  
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.ApplicationPresenter injectee) {
    injector.getFragment_com_gwtplatform_mvp_client().com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection(injectee, injector.getFragment_com_gwtplatform_mvp_client().get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    
  }
  
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$Binder$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.ApplicationView.Binder injectee) {
    
  }
  
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$(com.gwtplatform.samples.basic.client.application.ApplicationView injectee) {
    
  }
  
  
  /**
   * Binding for com.gwtplatform.samples.basic.client.application.ApplicationPresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  public com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyView get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyView$_annotation$$none$$() {
    com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyView result = get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$();
    return result;
    
  }
  
  private com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$ == null) {
    Object created = GWT.create(com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy.class);
    assert created instanceof com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy;
    com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy result = (com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy) created;
    
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$;
    
  }
  
  private com.gwtplatform.samples.basic.client.application.ApplicationPresenter singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.client.application.ApplicationPresenter get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$ == null) {
    com.gwtplatform.samples.basic.client.application.ApplicationPresenter result = com$gwtplatform$samples$basic$client$application$ApplicationPresenter_com$gwtplatform$samples$basic$client$application$ApplicationPresenter_methodInjection(injector.getFragment_com_google_web_bindery_event_shared().get_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyView$_annotation$$none$$(), get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$(), injector.getFragment_com_gwtplatform_mvp_client_proxy().get_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$());
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$;
    
  }
  
  public com.gwtplatform.samples.basic.client.application.ApplicationPresenter com$gwtplatform$samples$basic$client$application$ApplicationPresenter_com$gwtplatform$samples$basic$client$application$ApplicationPresenter_methodInjection(com.google.web.bindery.event.shared.EventBus _0, com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyView _1, com.gwtplatform.samples.basic.client.application.ApplicationPresenter.MyProxy _2, com.gwtplatform.mvp.client.proxy.PlaceManager _3) {
    return new com.gwtplatform.samples.basic.client.application.ApplicationPresenter(_0, _1, _2, _3);
  }
  
  
  /**
   * Binding for com.google.inject.Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter> declared at:
   *   Implicit provider for com.google.inject.Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter>
   */
  public com.google.inject.Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter> get_Key$type$com$google$inject$Provider$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$$_annotation$$none$$() {
    com.google.inject.Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter> result = new com.google.inject.Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter>() { 
      public com.gwtplatform.samples.basic.client.application.ApplicationPresenter get() { 
        return get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$_annotation$$none$$();
      }
    };
    return result;
    
  }
  
  
  /**
   * Binding for com.gwtplatform.samples.basic.client.application.ApplicationView$Binder declared at:
   *   Implicit GWT.create binding for com.gwtplatform.samples.basic.client.application.ApplicationView$Binder
   */
  public com.gwtplatform.samples.basic.client.application.ApplicationView.Binder get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$Binder$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.samples.basic.client.application.ApplicationView.Binder.class);
    assert created instanceof com.gwtplatform.samples.basic.client.application.ApplicationView.Binder;
    com.gwtplatform.samples.basic.client.application.ApplicationView.Binder result = (com.gwtplatform.samples.basic.client.application.ApplicationView.Binder) created;
    
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$Binder$_annotation$$none$$(result);
    
    return result;
    
  }
  
  private com.gwtplatform.samples.basic.client.application.ApplicationView singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$ = null;
  
  public com.gwtplatform.samples.basic.client.application.ApplicationView get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$() {
    
    if (singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$ == null) {
    com.gwtplatform.samples.basic.client.application.ApplicationView result = com$gwtplatform$samples$basic$client$application$ApplicationView_com$gwtplatform$samples$basic$client$application$ApplicationView_methodInjection(get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$Binder$_annotation$$none$$());
    memberInject_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$(result);
    
        singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$ = result;
    }
    return singleton_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationView$_annotation$$none$$;
    
  }
  
  public com.gwtplatform.samples.basic.client.application.ApplicationView com$gwtplatform$samples$basic$client$application$ApplicationView_com$gwtplatform$samples$basic$client$application$ApplicationView_methodInjection(com.gwtplatform.samples.basic.client.application.ApplicationView.Binder _0) {
    return new com.gwtplatform.samples.basic.client.application.ApplicationView(_0);
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
    //   Implicit GWT.create binding for com.gwtplatform.samples.basic.client.application.ApplicationPresenter$MyProxy
    get_Key$type$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$MyProxy$_annotation$$none$$();
    
  }
  
}
