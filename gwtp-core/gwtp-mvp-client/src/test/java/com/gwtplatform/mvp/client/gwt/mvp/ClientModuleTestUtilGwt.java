/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.client.gwt.mvp;

import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.common.client.ClientUrlUtils;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;

import jakarta.inject.Singleton;

public class ClientModuleTestUtilGwt extends AbstractPresenterModule {
    @Override
    protected void configure() {
        // Singletons
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceManager.class).to(PlaceManagerTestUtilGwt.class).in(Singleton.class);
        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(RootPresenter.class).asEagerSingleton();

        bind(UrlUtils.class).to(ClientUrlUtils.class).in(Singleton.class);

        // Presenters
        bindPresenter(MainPresenterTestUtilGwt.class, MainPresenterTestUtilGwt.MyView.class,
                MainViewTestUtilGwt.class, MainPresenterTestUtilGwt.MyProxy.class);
        bindPresenter(AdminPresenterTestUtilGwt.class, AdminPresenterTestUtilGwt.MyView.class,
                AdminViewTestUtilGwt.class, AdminPresenterTestUtilGwt.MyProxy.class);

        bindSingletonPresenterWidget(PopupPresenterTestUtilGwt.class, PopupPresenterTestUtilGwt.MyView.class,
                PopupViewTestUtilGwt.class);

        // For testing
        bind(InstantiationCounterTestUtilGwt.class).asEagerSingleton();
        bindConstant().annotatedWith(Names.named("notice")).to("Hello");
    }
}
