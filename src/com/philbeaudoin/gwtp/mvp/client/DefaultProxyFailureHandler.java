package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyFailureHandler;

public class DefaultProxyFailureHandler implements ProxyFailureHandler {

  @Inject
  public DefaultProxyFailureHandler() {
  }
  
  @Override
  public void onFailedGetPresenter(Throwable caught) {
    Window.alert( "Code load failed!" );
  }

}
