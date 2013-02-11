package com.gwtplatform.samples.basic.client.place;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector;

public class com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment {
  public void memberInject_Key$type$com$gwtplatform$samples$basic$client$place$ClientPlaceManager$_annotation$$none$$(com.gwtplatform.samples.basic.client.place.ClientPlaceManager injectee) {
    
  }
  
  
  /**
   * Binding for com.gwtplatform.samples.basic.client.place.ClientPlaceManager declared at:
   *   Implicit binding for com.gwtplatform.samples.basic.client.place.ClientPlaceManager
   */
  public com.gwtplatform.samples.basic.client.place.ClientPlaceManager get_Key$type$com$gwtplatform$samples$basic$client$place$ClientPlaceManager$_annotation$$none$$() {
    com.gwtplatform.samples.basic.client.place.ClientPlaceManager result = com$gwtplatform$samples$basic$client$place$ClientPlaceManager_com$gwtplatform$samples$basic$client$place$ClientPlaceManager_methodInjection(injector.getFragment_com_google_web_bindery_event_shared().get_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$(), injector.getFragment_com_gwtplatform_mvp_client_proxy().get_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$());
    memberInject_Key$type$com$gwtplatform$samples$basic$client$place$ClientPlaceManager$_annotation$$none$$(result);
    
    return result;
    
  }
  
  public com.gwtplatform.samples.basic.client.place.ClientPlaceManager com$gwtplatform$samples$basic$client$place$ClientPlaceManager_com$gwtplatform$samples$basic$client$place$ClientPlaceManager_methodInjection(com.google.web.bindery.event.shared.EventBus _0, com.gwtplatform.mvp.client.proxy.TokenFormatter _1) {
    return new com.gwtplatform.samples.basic.client.place.ClientPlaceManager(_0, _1);
  }
  
  
  /**
   * Field for the enclosing injector.
   */
  private final com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector;
  public com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector_fragment(com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector injector) {
    this.injector = injector;
  }
  
}
