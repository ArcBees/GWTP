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

package com.gwtplatform.carstore.client.application.widget.header;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.carstore.client.resources.WidgetResources;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.dto.UserDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import static com.google.gwt.query.client.GQuery.$;

public class HeaderView extends ViewWithUiHandlers<HeaderUiHandlers> implements HeaderPresenter.MyView {
    interface Binder extends UiBinder<Widget, HeaderView> {
    }

    @UiField
    InlineLabel name;
    @UiField
    Button logout;
    @UiField
    HTMLPanel userOptions;
    @UiField
    DivElement menubar;

    private final WidgetResources widgetRes;

    @Inject
    HeaderView(Binder uiBinder, WidgetResources widgetResources) {
        this.widgetRes = widgetResources;

        initWidget(uiBinder.createAndBindUi(this));

        userOptions.setVisible(false);
    }

    @Override
    public void enableUserOptions(CurrentUser currentUser) {
        userOptions.setVisible(true);
        UserDto userDto = currentUser.getUser();
        name.setText(userDto.getFirstName());
    }

    @Override
    public void disableUserOptions() {
        userOptions.setVisible(false);
    }

    @Override
    public void setActive(String nameToken) {
        $("a", menubar).toggleClass(widgetRes.header().menuActive(), false);
        $("a[href*=\"" + nameToken + "\"]", menubar).toggleClass(widgetRes.header().menuActive(), true);

    }

    @UiHandler("logout")
    @SuppressWarnings("unused")
    void onLogoutClicked(ClickEvent event) {
        getUiHandlers().logout();
    }
}
