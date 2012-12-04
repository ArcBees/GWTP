/**
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

package com.gwtplatform.samples.tab.client.application;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.samples.tab.client.application.adminarea.AdminAreaPresenter;
import com.gwtplatform.samples.tab.client.application.adminarea.AdminAreaView;
import com.gwtplatform.samples.tab.client.application.dialog.DialogSamplePresenter;
import com.gwtplatform.samples.tab.client.application.dialog.DialogSampleView;
import com.gwtplatform.samples.tab.client.application.globaldialog.GlobalDialogPresenterWidget;
import com.gwtplatform.samples.tab.client.application.globaldialog.GlobalDialogSubTabPresenter;
import com.gwtplatform.samples.tab.client.application.globaldialog.GlobalDialogSubTabView;
import com.gwtplatform.samples.tab.client.application.globaldialog.GlobalDialogView;
import com.gwtplatform.samples.tab.client.application.home.HomePresenter;
import com.gwtplatform.samples.tab.client.application.home.HomeView;
import com.gwtplatform.samples.tab.client.application.homeinfo.HomeInfoPresenter;
import com.gwtplatform.samples.tab.client.application.homeinfo.HomeInfoView;
import com.gwtplatform.samples.tab.client.application.homenews.HomeNewsPresenter;
import com.gwtplatform.samples.tab.client.application.homenews.HomeNewsView;
import com.gwtplatform.samples.tab.client.application.infopopup.InfoPopupPresenterWidget;
import com.gwtplatform.samples.tab.client.application.infopopup.InfoPopupView;
import com.gwtplatform.samples.tab.client.application.localdialog.LocalDialogPresenterWidget;
import com.gwtplatform.samples.tab.client.application.localdialog.LocalDialogSubTabPresenter;
import com.gwtplatform.samples.tab.client.application.localdialog.LocalDialogSubTabView;
import com.gwtplatform.samples.tab.client.application.localdialog.LocalDialogView;
import com.gwtplatform.samples.tab.client.application.settings.SettingsPresenter;
import com.gwtplatform.samples.tab.client.application.settings.SettingsView;
import com.gwtplatform.samples.tab.client.application.ui.UiModule;

/**
 * @author Brandon Donnelson
 */
public class ApplicationModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    install(new UiModule());

    // Applicaiton Presenters
    bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
        ApplicationPresenter.MyProxy.class);
    bindPresenter(HomePresenter.class, HomePresenter.MyView.class, HomeView.class, HomePresenter.MyProxy.class);
    bindPresenter(DialogSamplePresenter.class, DialogSamplePresenter.MyView.class, DialogSampleView.class,
        DialogSamplePresenter.MyProxy.class);
    bindPresenter(LocalDialogSubTabPresenter.class, LocalDialogSubTabPresenter.MyView.class,
        LocalDialogSubTabView.class, LocalDialogSubTabPresenter.MyProxy.class);
    bindPresenter(GlobalDialogSubTabPresenter.class, GlobalDialogSubTabPresenter.MyView.class,
        GlobalDialogSubTabView.class, GlobalDialogSubTabPresenter.MyProxy.class);
    bindPresenter(SettingsPresenter.class, SettingsPresenter.MyView.class, SettingsView.class,
        SettingsPresenter.MyProxy.class);
    bindPresenter(AdminAreaPresenter.class, AdminAreaPresenter.MyView.class, AdminAreaView.class,
        AdminAreaPresenter.MyProxy.class);
    bindPresenter(HomeNewsPresenter.class, HomeNewsPresenter.MyView.class, HomeNewsView.class,
        HomeNewsPresenter.MyProxy.class);
    bindPresenter(HomeInfoPresenter.class, HomeInfoPresenter.MyView.class, HomeInfoView.class,
        HomeInfoPresenter.MyProxy.class);
    bindSingletonPresenterWidget(InfoPopupPresenterWidget.class, InfoPopupPresenterWidget.MyView.class,
        InfoPopupView.class);
    bindSingletonPresenterWidget(LocalDialogPresenterWidget.class, LocalDialogPresenterWidget.MyView.class,
        LocalDialogView.class);
    bindSingletonPresenterWidget(GlobalDialogPresenterWidget.class, GlobalDialogPresenterWidget.MyView.class,
        GlobalDialogView.class);
  }
}
