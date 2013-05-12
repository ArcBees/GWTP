/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.cars.car.navigation;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class NavigationTabView extends ViewWithUiHandlers<NavigationUiHandlers> implements
        NavigationTabPresenter.MyView {
    interface Binder extends UiBinder<Widget, NavigationTabView> {
    }

    @UiField
    TabBar tabBar;

    @Inject
    NavigationTabView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void createTab(Widget tabElement) {
        tabBar.addTab(tabElement);
    }

    @Override
    public void removeTab(int index) {
        tabBar.removeTab(index);
    }

    @Override
    public void selectTab(int index) {
        tabBar.selectTab(index);
    }

    @UiHandler("tabBar")
    void onTabBarSelection(SelectionEvent<Integer> event) {
        getUiHandlers().onTabSelected(event.getSelectedItem());
    }
}
