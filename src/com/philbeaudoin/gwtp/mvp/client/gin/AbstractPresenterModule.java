package com.philbeaudoin.gwtp.mvp.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.mvp.client.View;
import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.PresenterWidget;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;

public abstract class AbstractPresenterModule extends AbstractGinModule {

  public AbstractPresenterModule() {
    super();
  }

  /**
   * Convenience method for binding a non-singleton presenter with its view.
   *
   * @param <P>         The {@link PresenterWidget} type.
   * @param <V>         The {@link View} type.
   * @param presenter   The {@link PresenterWidget} (NOT a singleton).
   * @param view     The {@link View} interface.
   * @param viewImpl The {@link View} implementation (NOT a singleton).
   */
  protected <P extends PresenterWidget, V extends View> void bindPresenterWidget( 
      Class<P> presenter, 
      Class<V> view,
      Class<? extends V> viewImpl ) {
    bind( presenter );
    bind( viewImpl );
    bind( view ).to( viewImpl );
  }

  /**
   * Convenience method for binding a singleton presenter with its view and 
   * its proxy, when using automatically generated proxy classes.
   *
   * @param <P>         The {@link Presenter} type.
   * @param <V>         The {@link View} type.
   * @param <Proxy_>    The {@link Proxy} type.
   * @param presenter   The {@link Presenter} (a singleton).
   * @param view     The {@link View} interface.
   * @param viewImpl The {@link View} implementation (a singleton).
   * @param proxy       The {@link Proxy} interface, which will lead to an automatically 
   *                    generated proxy classes.
   * 
   * @see #bindPresenter(Class, Class, Class, Class, Class)
   */
  protected <P extends Presenter, V extends View, Proxy_ extends Proxy<P>> void bindPresenter( 
      Class<P> presenter, 
      Class<V> view,
      Class<? extends V> viewImpl,
      Class<Proxy_> proxy ) {
    bind( presenter ).in( Singleton.class );
    bind( viewImpl ).in( Singleton.class );
    bind( proxy ).asEagerSingleton();
    bind( view ).to( viewImpl );
  }
  
  /**
   * Convenience method for binding a singleton presenter with its view and 
   * its proxy, when using custom-made proxy classes.
   *
   * @param <P>         The {@link Presenter} type.
   * @param <V>         The {@link View} type.
   * @param <Proxy_>    The {@link Proxy} type.
   * @param presenter   The {@link Presenter} (a singleton).
   * @param view     The {@link View} interface.
   * @param viewImpl The {@link View} implementation (a singleton).
   * @param proxy       The {@link Proxy} interface.
   * @param proxyImpl   The {@link Proxy} implementation (a singleton).
   * 
   * @see #bindPresenter(Class, Class, Class, Class)
   */
  protected <P extends Presenter, V extends View, Proxy_ extends Proxy<P>> void bindPresenter( 
      Class<P> presenter, 
      Class<V> view,
      Class<? extends V> viewImpl,
      Class<Proxy_> proxy,
      Class<? extends Proxy_> proxyImpl ) {
    bind( presenter ).in( Singleton.class );
    bind( viewImpl ).in( Singleton.class );
    bind( proxyImpl ).asEagerSingleton();
    bind( view ).to( viewImpl );
    bind( proxy ).to( proxyImpl );
  }

}