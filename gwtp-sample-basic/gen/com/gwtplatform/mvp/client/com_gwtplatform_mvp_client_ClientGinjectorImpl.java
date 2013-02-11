package com.gwtplatform.mvp.client;

public class com_gwtplatform_mvp_client_ClientGinjectorImpl implements com.gwtplatform.mvp.client.ClientGinjector {
  
  /**
   * Top-level injector instance for injector interface com.gwtplatform.mvp.client.ClientGinjector.
   */
  private final com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector = new com_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector(this);
  public com_gwtplatform_mvp_client_ClientGinjectorImpl() {
    fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector.initializeEagerSingletons();
    
  }
  
  public com.gwtplatform.samples.basic.client.gin.BootstrapperImpl getBootstrapperImpl() {
    return fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector.getFragment_com_gwtplatform_samples_basic_client_gin().get_Key$type$com$gwtplatform$samples$basic$client$gin$BootstrapperImpl$_annotation$$none$$();
  }
  
  public com.google.web.bindery.event.shared.EventBus getEventBus() {
    return fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector.getFragment_com_google_web_bindery_event_shared().get_Key$type$com$google$web$bindery$event$shared$EventBus$_annotation$$none$$();
  }
  
  public com.gwtplatform.mvp.client.proxy.PlaceManager getPlaceManager() {
    return fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector.getFragment_com_gwtplatform_mvp_client_proxy().get_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$();
  }
  
  public javax.inject.Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter> getcomgwtplatformsamplesbasicclientapplicationApplicationPresenter() {
    return fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector.getFragment_com_gwtplatform_samples_basic_client_application().get_Key$type$com$google$inject$Provider$com$gwtplatform$samples$basic$client$application$ApplicationPresenter$$_annotation$$none$$();
  }
  
  public com.google.gwt.inject.client.AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter> getcomgwtplatformsamplesbasicclientapplicationresponseResponsePresenter() {
    return fieldcom_gwtplatform_mvp_client_ClientGinjector_ClientGinjectorGinjector.getFragment_com_gwtplatform_samples_basic_client_application_response().get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$gwtplatform$samples$basic$client$application$response$ResponsePresenter$$_annotation$$none$$();
  }
  
}
