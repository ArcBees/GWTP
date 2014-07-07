/**
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

package com.gwtplatform.carstore.client.application;

import com.google.gwt.user.client.History;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.place.ParameterTokens;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;

public class UnauthorizedPresenter extends Presenter<UnauthorizedPresenter.MyView, UnauthorizedPresenter.MyProxy> {
    interface MyView extends View {
        void setLinkToLogin(String link);
    }

    @ProxyStandard
    @NameToken(NameTokens.UNAUTHORIZED)
    @NoGatekeeper
    interface MyProxy extends ProxyPlace<UnauthorizedPresenter> {
    }

    private final TokenFormatter tokenFormatter;

    @Inject
    UnauthorizedPresenter(EventBus eventBus,
                          MyView view,
                          MyProxy proxy,
                          TokenFormatter tokenFormatter) {
        super(eventBus, view, proxy, RevealType.RootLayout);

        this.tokenFormatter = tokenFormatter;
    }

    @Override
    protected void onReveal() {
        PlaceRequest request = new PlaceRequest.Builder()
                .nameToken(NameTokens.LOGIN)
                .with(ParameterTokens.REDIRECT, History.getToken())
                .build();

        getView().setLinkToLogin(tokenFormatter.toPlaceToken(request));
    }
}
