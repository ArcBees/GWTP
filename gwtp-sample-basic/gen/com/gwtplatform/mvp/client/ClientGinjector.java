package com.gwtplatform.mvp.client;

import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.inject.client.GinModules;
import com.gwtplatform.samples.basic.client.gin.ClientModule;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.samples.basic.client.gin.BootstrapperImpl;
import javax.inject.Provider;
import com.google.gwt.inject.client.AsyncProvider;

@GinModules({ClientModule.class})
public interface ClientGinjector extends Ginjector {
  static ClientGinjector SINGLETON = GWT.create(ClientGinjector.class);
  
  EventBus getEventBus();
  
  PlaceManager getPlaceManager();
  
  BootstrapperImpl getBootstrapperImpl();
  
  Provider<com.gwtplatform.samples.basic.client.application.ApplicationPresenter> getcomgwtplatformsamplesbasicclientapplicationApplicationPresenter();
  
  AsyncProvider<com.gwtplatform.samples.basic.client.application.response.ResponsePresenter> getcomgwtplatformsamplesbasicclientapplicationresponseResponsePresenter();
}
