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

package com.gwtplatform.carstore.client.application.cars.car.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class Tab extends Composite implements CloseTabEvent.HasCloseTabHandlers {
    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, Tab> {
    }

    @UiField
    InlineLabel nameLabel;
    @UiField
    InlineLabel closeButton;

    public Tab(String name,
               boolean isClosable) {
        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setText(name);
        closeButton.setVisible(isClosable);
    }

    @UiHandler("closeButton")
    void onCloseButtonClicked(ClickEvent event) {
        CloseTabEvent.fire(this);
    }

    @Override
    public HandlerRegistration addCloseTabHandler(CloseTabEvent.CloseTabHandler handler) {
        return addHandler(handler, CloseTabEvent.getType());
    }
}
