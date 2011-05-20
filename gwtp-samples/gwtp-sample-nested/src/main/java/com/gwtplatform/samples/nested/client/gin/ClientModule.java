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

package com.gwtplatform.samples.nested.client.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.samples.nested.client.GwtpnestedsamplePlaceManager;
import com.gwtplatform.samples.nested.client.NameTokens;
import com.gwtplatform.samples.nested.client.presenter.AboutUsPresenter;
import com.gwtplatform.samples.nested.client.presenter.ContactPresenter;
import com.gwtplatform.samples.nested.client.presenter.HomePresenter;
import com.gwtplatform.samples.nested.client.presenter.MainPagePresenter;
import com.gwtplatform.samples.nested.client.view.AboutUsView;
import com.gwtplatform.samples.nested.client.view.ContactView;
import com.gwtplatform.samples.nested.client.view.HomeView;
import com.gwtplatform.samples.nested.client.view.MainPageView;

/**
 * @author Christian Goudreau
 */
public class ClientModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Default implementation of standard resources
    install(new DefaultModule(GwtpnestedsamplePlaceManager.class));

    // Constants
    bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.homePage);

    // Presenters
    bindPresenter(MainPagePresenter.class, MainPagePresenter.MyView.class,
        MainPageView.class, MainPagePresenter.MyProxy.class);
    bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
        HomeView.class, HomePresenter.MyProxy.class);
    bindPresenter(AboutUsPresenter.class, AboutUsPresenter.MyView.class,
        AboutUsView.class, AboutUsPresenter.MyProxy.class);
    bindPresenter(ContactPresenter.class, ContactPresenter.MyView.class,
        ContactView.class, ContactPresenter.MyProxy.class);
  }
}