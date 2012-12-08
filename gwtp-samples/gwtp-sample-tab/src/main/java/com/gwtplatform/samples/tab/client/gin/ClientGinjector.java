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

package com.gwtplatform.samples.tab.client.gin;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.samples.tab.client.MyConstants;
import com.gwtplatform.samples.tab.client.application.adminarea.AdminAreaPresenter;
import com.gwtplatform.samples.tab.client.application.dialog.DialogSamplePresenter;
import com.gwtplatform.samples.tab.client.application.globaldialog.GlobalDialogSubTabPresenter;
import com.gwtplatform.samples.tab.client.application.homeinfo.HomeInfoPresenter;
import com.gwtplatform.samples.tab.client.application.homenews.HomeNewsPresenter;
import com.gwtplatform.samples.tab.client.application.localdialog.LocalDialogSubTabPresenter;
import com.gwtplatform.samples.tab.client.application.settings.SettingsPresenter;
import com.gwtplatform.samples.tab.client.application.ui.UiModule;

/**
 * The main {@link com.google.gwt.inject.client.Ginjector Ginjector} of our
 * application.
 * 
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
@GinModules({ ClientModule.class, UiModule.class })
public interface ClientGinjector extends ClientGinjectorBase {
  AsyncProvider<DialogSamplePresenter> getDialogSamplePresenter();

  AsyncProvider<LocalDialogSubTabPresenter> getLocalDialogSubTabPresenter();

  AsyncProvider<GlobalDialogSubTabPresenter> getGlobalDialogSubTabPresenter();

  AsyncProvider<AdminAreaPresenter> getAdminAreaPresenter();

  AsyncProvider<SettingsPresenter> getSettingsPresenter();

  AsyncProvider<HomeInfoPresenter> getHomeInfoPresenter();

  AsyncProvider<HomeNewsPresenter> getHomeNewsPresenter();

  PlaceManager getPlaceManager();

  MyConstants getMyConstants();

  // TODO fix this so it doesn't us UiBinder injection, or GinBinder injection.
  // Revisit this later.
  // The following methods allow these widgets to be used in UiBinder files
  // and participate in dependency injection.
  // LinkMenu getLinkMenu();
  // RoundTabPanel getRoundTabPanel();
  // SimpleTabPanel getSimpleTabPanel();
}
