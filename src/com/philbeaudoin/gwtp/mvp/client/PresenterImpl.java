package com.philbeaudoin.gwtp.mvp.client;

import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceRequest;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;

public abstract class PresenterImpl<V extends View, Proxy_ extends Proxy<?>> 
extends PresenterWidgetImpl<V> implements Presenter {

  /**
   * The light-weight {@PresenterProxy} around this presenter.
   */
  protected final Proxy_ proxy;
  
  /**
   * Creates a basic {@link Presenter}.
   * @param eventBus The event bus.
   * @param view  The view attached to this presenter.
   * @param proxy The presenter proxy.
   */
  public PresenterImpl( 
      final EventBus eventBus, 
      final V view, 
      final Proxy_ proxy ) {
    super(eventBus, view);
    this.proxy = proxy;
  }
  
  @Override
  public final Proxy_ getProxy() {
    return proxy;
  }
  
  @Override
  public void reveal() {
    getProxy().reveal();
  }
  
  @Override
  public final void forceReveal() {
    if( isVisible() )
      return;
    revealInParent();
  }
  
  @Override
  public void onReset() {
    super.onReset();
    getProxy().onPresenterRevealed( this );
  }
  
  /**
   * Called whenever the presenter needs to set its content in 
   * a parent. Should usually fire a {@link RevealContentEvent}.
   */
  protected abstract void revealInParent();

  /**
   * Notify others that this presenter has been changed. This is especially
   * useful for stateful presenters that store parameters within the
   * history token. Calling this will make sure the history token is
   * updated with the right parameters.
   */
  protected final void notifyChange() {
    getProxy().onPresenterChanged( this );
  }
  
  @Override
  public void prepareFromRequest(PlaceRequest request) {
    // By default, no parameter to extract from request.
  }

  @Override
  public PlaceRequest prepareRequest(PlaceRequest request) {
    // By default, no parameter to add to request
    return request;
  }

  
}
