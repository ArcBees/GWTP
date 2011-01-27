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

package com.gwtplatform.samples.tab.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.gwtplatform.samples.tab.client.CurrentUser;
import com.gwtplatform.samples.tab.client.FailureHandlerAlert;
import com.gwtplatform.samples.tab.client.GwtptabsamplePlaceManager;
import com.gwtplatform.samples.tab.client.IsAdminGatekeeper;
import com.gwtplatform.samples.tab.client.MyConstants;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.presenter.AdminAreaPresenter;
import com.gwtplatform.samples.tab.client.presenter.DialogSamplePresenter;
import com.gwtplatform.samples.tab.client.presenter.GlobalDialogPresenterWidget;
import com.gwtplatform.samples.tab.client.presenter.HomeInfoPresenter;
import com.gwtplatform.samples.tab.client.presenter.HomeNewsPresenter;
import com.gwtplatform.samples.tab.client.presenter.HomePresenter;
import com.gwtplatform.samples.tab.client.presenter.LocalDialogPresenterWidget;
import com.gwtplatform.samples.tab.client.presenter.MainPagePresenter;
import com.gwtplatform.samples.tab.client.presenter.PopupPresenterWidget;
import com.gwtplatform.samples.tab.client.presenter.SettingsPresenter;
import com.gwtplatform.samples.tab.client.view.AdminAreaView;
import com.gwtplatform.samples.tab.client.view.DialogSampleView;
import com.gwtplatform.samples.tab.client.view.GlobalDialogView;
import com.gwtplatform.samples.tab.client.view.HomeInfoView;
import com.gwtplatform.samples.tab.client.view.HomeNewsView;
import com.gwtplatform.samples.tab.client.view.HomeView;
import com.gwtplatform.samples.tab.client.view.InfoPopupView;
import com.gwtplatform.samples.tab.client.view.LocalDialogView;
import com.gwtplatform.samples.tab.client.view.MainPageView;
import com.gwtplatform.samples.tab.client.view.SettingsView;

/**
 * @author Christian Goudreau
 */
public class ClientModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Singletons
    bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
    bind(PlaceManager.class).to(GwtptabsamplePlaceManager.class).in(
        Singleton.class);
    bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(
        Singleton.class);
    bind(RootPresenter.class).asEagerSingleton();
    bind(ProxyFailureHandler.class).to(FailureHandlerAlert.class).in(
        Singleton.class);
    bind(MyConstants.class).in(Singleton.class);
    bind(CurrentUser.class).in(Singleton.class);
    bind(IsAdminGatekeeper.class).in(Singleton.class);
    
    // Constants
    bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.homePage);

    // Presenters
    bindPresenter(MainPagePresenter.class, MainPagePresenter.MyView.class,
        MainPageView.class, MainPagePresenter.MyProxy.class);
    bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
        HomeView.class, HomePresenter.MyProxy.class);
    bindPresenter(DialogSamplePresenter.class, DialogSamplePresenter.MyView.class,
        DialogSampleView.class, DialogSamplePresenter.MyProxy.class);
    bindPresenter(SettingsPresenter.class, SettingsPresenter.MyView.class,
        SettingsView.class, SettingsPresenter.MyProxy.class);
    bindPresenter(AdminAreaPresenter.class, AdminAreaPresenter.MyView.class,
        AdminAreaView.class, AdminAreaPresenter.MyProxy.class);
    bindPresenter(HomeNewsPresenter.class, HomeNewsPresenter.MyView.class,
        HomeNewsView.class, HomeNewsPresenter.MyProxy.class);
    bindPresenter(HomeInfoPresenter.class, HomeInfoPresenter.MyView.class,
        HomeInfoView.class, HomeInfoPresenter.MyProxy.class);
    bindSingletonPresenterWidget(PopupPresenterWidget.class,
        PopupPresenterWidget.MyView.class, InfoPopupView.class);
    bindSingletonPresenterWidget(LocalDialogPresenterWidget.class,
        LocalDialogPresenterWidget.MyView.class, LocalDialogView.class);
    bindSingletonPresenterWidget(GlobalDialogPresenterWidget.class,
        GlobalDialogPresenterWidget.MyView.class, GlobalDialogView.class);
  }
}