/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.mvp.client.gwt.mvp;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;

import jakarta.inject.Inject;

public class PopupViewTestUtilGwt extends PopupViewImpl implements PopupPresenterTestUtilGwt.MyView {
    public interface Binder extends UiBinder<Widget, PopupViewTestUtilGwt> {
    }

    @UiField
    DialogBox mainSlot;

    @Inject
    PopupViewTestUtilGwt(EventBus eventBus, Binder uiBinder) {
        super(eventBus);

        initWidget(uiBinder.createAndBindUi(this));
    }
}
