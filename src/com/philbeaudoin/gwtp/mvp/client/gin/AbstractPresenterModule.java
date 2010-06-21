/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.mvp.client.PresenterImpl;
import com.philbeaudoin.gwtp.mvp.client.View;
import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.PresenterWidget;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;

public abstract class AbstractPresenterModule extends AbstractGinModule {

  public AbstractPresenterModule() {
    super();
  }

  /**
   * Convenience method for binding a non-singleton {@link PresenterWidget} with its {@link View}.
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
    bind( view ).to( viewImpl );
  }

  /**
   * Convenience method for binding a non-singleton {@link PresenterWidget} that
   * is created via a factory to its {@link View}, which is also created via
   * a factory.
   *
   * @param <P> The type of the {@link PresenterWidget} factory.
   * @param <V>  The type of the {@link View} factory.
   * @param presenterFactory The interface to the {@link PresenterWidget} factory.
   * @param presenterFactoryImpl The implementation of the {@link PresenterWidget} factory.
   * @param viewFactory The interface to the {@link View} factory.
   * @param viewFactoryImpl The implementation of the {@link View} factory.
   */
  protected <P, V> void bindPresenterWidgetFactory(
      Class<P> presenterFactory,
      Class<? extends P> presenterFactoryImpl,
      Class<V> viewFactory,
      Class<? extends V> viewFactoryImpl ) {
    bind(presenterFactory).to(presenterFactoryImpl).in(Singleton.class);
    bind(viewFactory).to(viewFactoryImpl).in(Singleton.class);
  }

  /**
   * Convenience method for binding a singleton {@link PresenterWidget} with its {@link View}.
   * <p />
   * <b>Important!</b> If you want to use the same {@link PresenterWidget} in many different places,
   * you should consider making it non-singleton with {@link #bindPresenterWidget}. It is possible
   * to use the same singleton {@link PresenterWidget} in different presenters, as long as these
   * are not simultaneously visible. Also, if you do this, you must make sure to set the singleton
   * presenter widget as content in its containing presenter {@link PresenterImpl#onReveal} and to
   * remove it in the {@link PresenterImpl#onHide}.
   *
   * @param <P>         The {@link PresenterWidget} type.
   * @param <V>         The {@link View} type.
   * @param presenter   The {@link PresenterWidget} (a singleton).
   * @param view     The {@link View} interface.
   * @param viewImpl The {@link View} implementation (a singleton).
   */
  protected <P extends PresenterWidget, V extends View> void bindSingletonPresenterWidget( 
      Class<P> presenter, 
      Class<V> view,
      Class<? extends V> viewImpl ) {
    bind( presenter ).in(Singleton.class);
    bind( view ).to( viewImpl ).in(Singleton.class);
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
  
  /**
   * Convenience method for binding a singleton presenter with
   * its proxy, when using automatically generated proxy classes and non-singleton views.
   * <p />
   * <b>Important!</b> This is only be meant to be used by presenters associated 
   * with non-singleton views, for example when the same view class is reused with 
   * many presenters. As such, you will need to also use the {@link #bindSharedView} method.
   * If the view class is use only by one presenter, you should consider using
   * {@link #bindPresenter(Class, Class, Class, Class)} instead.
   *
   * @param <P>         The {@link Presenter} type.
   * @param <Proxy_>    The {@link Proxy} type.
   * @param presenter   The {@link Presenter} (a singleton).
   * @param proxy       The {@link Proxy} interface, which will lead to an automatically 
   *                    generated proxy classes.
   * 
   * @see #bindPresenter(Class, Class, Class)
   */
  protected <P extends Presenter, Proxy_ extends Proxy<P>> void bindPresenter( 
      Class<P> presenter,
      Class<Proxy_> proxy ) {
    bind( presenter ).in( Singleton.class );
    bind( proxy ).asEagerSingleton();
  }

  /**
   * Convenience method for binding a singleton presenter with 
   * its proxy, when using custom-made proxy classes and non-singleton views.
   * <p />
   * <b>Important!</b> This is only be meant to be used by presenters associated 
   * with non-singleton views, for example when the same view class is reused with 
   * many presenters. As such, you will need to also use the {@link #bindSharedView} method.
   * If the view class is use only by one presenter, you should consider using
   * {@link #bindPresenter(Class, Class, Class, Class, Class)} instead.
   *
   * @param <P>         The {@link Presenter} type.
   * @param <Proxy_>    The {@link Proxy} type.
   * @param presenter   The {@link Presenter} (a singleton).
   * @param proxy       The {@link Proxy} interface.
   * @param proxyImpl   The {@link Proxy} implementation (a singleton).
   * 
   * @see #bindPresenter(Class, Class)
   */
  protected <P extends Presenter,Proxy_ extends Proxy<P>> void bindPresenter( 
      Class<P> presenter, 
      Class<Proxy_> proxy,
      Class<? extends Proxy_> proxyImpl ) {
    bind( presenter ).in( Singleton.class );
    bind( proxyImpl ).asEagerSingleton();
    bind( proxy ).to( proxyImpl );
  }

  /**
   * Bind a view interface to its implementation in a non-singleton manner.
   * <p />
   * <b>Important!</b> This is only be meant to be used for presenter associated 
   * with non-singleton views, for example when the same view class is reused with 
   * many presenters. As such, you will use this method with
   * {@link #bindPresenter(Class, Class)} or {@link #bindPresenter(Class, Class, Class)}.
   * If the view class is use only by one presenter, you should consider using
   * {@link #bindPresenter(Class, Class, Class, Class, Class)} instead.
   *
   * @param <V>      The {@link View} type.
   * @param view     The {@link View} interface.
   * @param viewImpl The {@link View} implementation (not a singleton).
   */
  protected <V extends View> void bindSharedView( 
      Class<V> view,
      Class<? extends V> viewImpl ) {
    bind( view ).to( viewImpl );
  }
}