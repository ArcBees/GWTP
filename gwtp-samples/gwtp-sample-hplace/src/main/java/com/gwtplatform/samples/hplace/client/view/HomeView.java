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

package com.gwtplatform.samples.hplace.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.samples.hplace.client.presenter.HomePresenter.MyView;

/**
 * @author Philippe Beaudoin
 */
public class HomeView extends ViewWithUiHandlers<HomeUiHandlers>
  implements MyView {

  interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
  }

  private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);

  @UiField
  Button all;

  @UiField
  Button favorites;

  @UiField
  Button specials;

  private final Widget widget;

  @Inject
  public HomeView() {
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @UiHandler("all")
  void onAllClicked(ClickEvent event) {
    getUiHandlers().revealAllProductsList();
  }

  @UiHandler("favorites")
  void onFavoritesClicked(ClickEvent event) {
    getUiHandlers().revealFavoriteProductsList();
  }

  @UiHandler("specials")
  void onSpecialsClicked(ClickEvent event) {
    getUiHandlers().revealSpecialsList();
  }

}