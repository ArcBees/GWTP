package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.philbeaudoin.gwtp.mvp.client.Presenter;

/**
 * The interface for light-weight singleton classes 
 * that listens for events before the full {@link Presenter} 
 * is instantiated. This include, among others, the presenter's 
 * specific {@link RevealContentEvent} that needs the presenter to 
 * reveal itself.
 * <p />
 * The relationship between a presenter and its proxy is two-way.
 * <p />
 * This class is called PresenterProxy instead of simply Proxy
 * because {@link Presenter} subclasses will usually define 
 * their own interface called Proxy and derived from this one. 
 * Naming this interface Proxy would therefore be impractical
 * for code-writing purposes.
 * 
 * @author beaudoin
 */
public interface Proxy<P extends Presenter> extends ProxyRaw {

  /**
   * Get the associated {@link Presenter}. The presenter can only be obtained in an asynchronous
   * manner to support code splitting when needed. To access the presenter, pass a callback.
   * 
   * @param callback The callback in which the {@link Presenter} will be passed as a parameter.
   */
  public void getPresenter( AsyncCallback<P> callback );
  
}