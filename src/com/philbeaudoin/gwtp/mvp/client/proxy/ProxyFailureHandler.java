/**
 * 
 */
package com.philbeaudoin.gwtp.mvp.client.proxy;

public interface ProxyFailureHandler {
  void onFailedGetPresenter(Throwable caught);
}