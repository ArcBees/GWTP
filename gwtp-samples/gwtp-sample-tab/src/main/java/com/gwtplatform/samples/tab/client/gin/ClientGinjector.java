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
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.samples.tab.client.MyConstants;
import com.gwtplatform.samples.tab.client.presenter.ContactPresenter;
import com.gwtplatform.samples.tab.client.presenter.DialogSamplePresenter;
import com.gwtplatform.samples.tab.client.presenter.HomeInfoPresenter;
import com.gwtplatform.samples.tab.client.presenter.HomeNewsPresenter;
import com.gwtplatform.samples.tab.client.presenter.HomePresenter;
import com.gwtplatform.samples.tab.client.presenter.MainPagePresenter;
import com.gwtplatform.samples.tab.client.ui.LinkMenu;
import com.gwtplatform.samples.tab.client.ui.RoundTabPanel;
import com.gwtplatform.samples.tab.client.ui.SimpleTabPanel;
import com.gwtplatform.samples.tab.client.ui.UiModule;

/**
 * @author Christian Goudreau
 */
@GinModules({ClientModule.class, UiModule.class})
public interface ClientGinjector extends Ginjector {
  AsyncProvider<DialogSamplePresenter> getAboutUsPresenter();
  AsyncProvider<ContactPresenter> getContactPresenter();
  EventBus getEventBus();
  AsyncProvider<HomeInfoPresenter> getHomeInfoPresenter();
  AsyncProvider<HomeNewsPresenter> getHomeNewsPresenter();
  AsyncProvider<HomePresenter> getHomePresenter();
  Provider<MainPagePresenter> getMainPagePresenter();
  PlaceManager getPlaceManager();
  ProxyFailureHandler getProxyFailureHandler();
  MyConstants getMyConstants();
  
  // The following methods allow our widget to participate in dependency injection
  LinkMenu getLinkMenu();
  RoundTabPanel getRoundTabPanel();
  SimpleTabPanel getSimpleTabPanel();
}