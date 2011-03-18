/**
 * Copyright 2010 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.samples.hplace.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;

import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.gwtplatform.samples.hplace.client.GwtphplacesamplePlaceManager;
import com.gwtplatform.samples.hplace.client.NameTokens;
import com.gwtplatform.samples.hplace.client.presenter.BreadcrumbsPresenter;
import com.gwtplatform.samples.hplace.client.presenter.HomePresenter;
import com.gwtplatform.samples.hplace.client.presenter.ProductListPresenter;
import com.gwtplatform.samples.hplace.client.presenter.ProductPresenter;
import com.gwtplatform.samples.hplace.client.view.BreadcrumbsView;
import com.gwtplatform.samples.hplace.client.view.HomeView;
import com.gwtplatform.samples.hplace.client.view.ProductListView;
import com.gwtplatform.samples.hplace.client.view.ProductView;

/**
 * @author Christian Goudreau
 */
public class ClientModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Singletons
    bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
    bind(PlaceManager.class).to(GwtphplacesamplePlaceManager.class).in(
        Singleton.class);
    bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(
        Singleton.class);
    bind(RootPresenter.class).asEagerSingleton();
    bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(
        Singleton.class);

    // Constants
    bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.homePage);

    // Presenters
    bindPresenter(BreadcrumbsPresenter.class,
        BreadcrumbsPresenter.MyView.class, BreadcrumbsView.class,
        BreadcrumbsPresenter.MyProxy.class);
    bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
        HomeView.class, HomePresenter.MyProxy.class);
    bindPresenter(ProductListPresenter.class,
        ProductListPresenter.MyView.class, ProductListView.class,
        ProductListPresenter.MyProxy.class);
    bindPresenter(ProductPresenter.class, ProductPresenter.MyView.class,
        ProductView.class, ProductPresenter.MyProxy.class);
  }
}