/**
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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.client.resources.AppResources;

public class UserInfoPopup extends PopupPanel {
    interface Binder extends UiBinder<Widget, UserInfoPopup> {
    }

    public interface Handler {
        void onLogout();
    }

    @UiField
    Label username;

    private Handler handler;

    @Inject
    UserInfoPopup(Binder uiBinder,
                  AppResources appResources) {
        setWidget(uiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setStyleName(appResources.styles().gwtPopupPanel());
    }

    public void setUsername(String login) {
        username.setText(login);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @UiHandler("logout")
    void onLogoutClicked(ClickEvent event) {
        handler.onLogout();
        hide();
    }
}
