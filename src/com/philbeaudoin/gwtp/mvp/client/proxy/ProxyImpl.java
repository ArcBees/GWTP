package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.philbeaudoin.gwtp.mvp.client.IndirectProvider;
import com.philbeaudoin.gwtp.mvp.client.Presenter;

public class ProxyImpl<P extends Presenter> 
implements Proxy<P> {

  protected ProxyFailureHandler failureHandler;
  protected IndirectProvider<P> presenter;

  /**
   * Creates a Proxy class for a specific presenter.
   */
  public ProxyImpl() {}

  /**
   * Injects the various resources and performs other bindings. 
   * <p />
   * Never call directly, it should only be called by GIN.
   * Method injection is used instead of constructor injection, because the 
   * latter doesn't work well with GWT generators.
   * 
   * @param failureHandler The {@link ProxyFailureHandler}.
   */
  @Inject
  protected void bind( ProxyFailureHandler failureHandler ) {
    this.failureHandler = failureHandler;    
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void getRawPresenter(AsyncCallback<Presenter> callback) {
    presenter.get((AsyncCallback<P>) callback);
  }
  
  @Override
  public void getPresenter( AsyncCallback<P> callback ) {
    presenter.get(callback);
  }

  /**
   * By default, proxys can't reveal their presenter. Only place
   * proxies, such as {@link ProxyPlaceAbstract} can. 
   */
  @Override
  public void reveal() {
    assert false : "Unrevealable proxy.";
  }
  
  @Override
  public void onPresenterChanged( Presenter presenter ) {
  }

  @Override
  public void onPresenterRevealed( Presenter presenter ) {
  }


}
