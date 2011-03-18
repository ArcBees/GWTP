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

package com.gwtplatform.samples.tab.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;

/**
 * The {@link PresenterWidget} of a dialog box that is meant to be
 * displayed no matter which presenter is visible. Compare
 * to {@link LocalDialogPresenterWidget}.
 *
 * @author Philippe Beaudoin
 */
public class GlobalDialogPresenterWidget extends
    PresenterWidget<GlobalDialogPresenterWidget.MyView>
    implements NavigationHandler  {

  /**
   * @author Philippe beaudoin
   */
  public interface MyView extends PopupView {
    void setNavigationPlace(String placeName);
  }

  private HandlerRegistration handlerRegistration;

  @Inject
  public GlobalDialogPresenterWidget(
      final EventBus eventBus,
      final MyView view) {
    super(eventBus, view);
  }

  @Override
  public void onReveal() {
    super.onReveal();
    getView().setNavigationPlace(null);
    unregisterNavigationHandler(); // Be on the safe side
    handlerRegistration = addHandler(NavigationEvent.getType(), this);
  }

  @Override
  public void onHide() {
    super.onHide();
    unregisterNavigationHandler();
  }

  @Override
  public void onNavigation(NavigationEvent navigationEvent) {
    getView().setNavigationPlace(navigationEvent.getRequest().getNameToken());
  }

  private void unregisterNavigationHandler() {
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
      handlerRegistration = null;
    }
  }
}
