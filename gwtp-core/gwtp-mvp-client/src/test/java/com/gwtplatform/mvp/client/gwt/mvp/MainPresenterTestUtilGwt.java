/*
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

package com.gwtplatform.mvp.client.gwt.mvp;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewCloseHandler;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

/**
 * A test presenter meant to be run in a GWTTestCase.
 */
public class MainPresenterTestUtilGwt
        extends Presenter<MainPresenterTestUtilGwt.MyView, MainPresenterTestUtilGwt.MyProxy> {

    interface MyView extends View {
    }

    @ProxyStandard
    @NameToken("home")
    interface MyProxy extends ProxyPlace<MainPresenterTestUtilGwt> {
    }

    private final Provider<PopupPresenterTestUtilGwt> popupPresenterProvider;

    @Inject
    MainPresenterTestUtilGwt(EventBus eventBus,
                             MyView view,
                             MyProxy proxy,
                             Provider<PopupPresenterTestUtilGwt> popupPresenterProvider) {
        super(eventBus, view, proxy, RevealType.Root);

        this.popupPresenterProvider = popupPresenterProvider;
    }

    public void showPopup(PopupViewCloseHandler closeHandler) {
        PopupPresenterTestUtilGwt popup = popupPresenterProvider.get();
        popup.setCloseHandler(closeHandler);
        addToPopupSlot(popup);
    }
}

