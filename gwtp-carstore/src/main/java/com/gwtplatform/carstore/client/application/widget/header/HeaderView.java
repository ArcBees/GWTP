/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.widget.header;

import java.util.Arrays;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ValuePicker;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.renderer.EnumCell;
import com.gwtplatform.carstore.client.resources.NavigationListStyle;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.dto.UserDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class HeaderView extends ViewWithUiHandlers<HeaderUiHandlers> implements HeaderPresenter.MyView {
    interface Binder extends UiBinder<Widget, HeaderView> {
    }

    @UiField
    InlineLabel name;
    @UiField
    Button logout;
    @UiField
    HTMLPanel userOptions;
    @UiField(provided = true)
    ValuePicker<MenuItem> menuBar;

    private final PlaceManager placeManager;

    @Inject
    HeaderView(Binder uiBinder,
               NavigationListStyle listResources,
               PlaceManager placeManager) {
        this.placeManager = placeManager;

        CellList<MenuItem> placeList = new CellList<MenuItem>(new EnumCell<MenuItem>(), listResources);
        menuBar = new ValuePicker<MenuItem>(placeList);

        initWidget(uiBinder.createAndBindUi(this));

        menuBar.setAcceptableValues(Arrays.asList(MenuItem.values()));
        menuBar.setValue(MenuItem.MANUFACTURER);

        menuBar.setVisible(false);
        userOptions.setVisible(false);
    }

    @Override
    public void enableUserOptions(CurrentUser currentUser) {
        userOptions.setVisible(true);
        menuBar.setVisible(true);
        UserDto userDto = currentUser.getUser();
        name.setText(userDto.getFirstName());
    }

    @Override
    public void disableUserOptions() {
        userOptions.setVisible(false);
        menuBar.setVisible(false);
    }

    @Override
    public void showActionBar(Boolean visible) {
    }

    @Override
    public void initActionBar(Boolean tabsVisible) {
    }

    @Override
    public void hideActionButtons() {
    }

    @Override
    public void showActionButton(ChangeActionBarEvent.ActionType actionType) {
    }

    @UiHandler("logout")
    @SuppressWarnings("unused")
    void onLogoutClicked(ClickEvent event) {
        getUiHandlers().logout();
    }

    @UiHandler("menuBar")
    void onMenuItemChanged(ValueChangeEvent<MenuItem> event) {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(menuBar.getValue().getPlaceToken()).build());
    }
}
