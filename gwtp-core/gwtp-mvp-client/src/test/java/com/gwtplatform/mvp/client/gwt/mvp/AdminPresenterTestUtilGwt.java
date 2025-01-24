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
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

import jakarta.inject.Inject;

/**
 * A test presenter meant to be run in a GWTTestCase.
 */
public class AdminPresenterTestUtilGwt extends Presenter<AdminPresenterTestUtilGwt.MyView,
        AdminPresenterTestUtilGwt.MyProxy> {

    /**
     * Presenter's view.
     */
    public interface MyView extends View {
    }

    /**
     * Presenter's proxy.
     */
    @ProxyStandard
    @NameToken({"admin","selfService"})
    public interface MyProxy extends ProxyPlace<AdminPresenterTestUtilGwt> {
    }

    @Inject
    public AdminPresenterTestUtilGwt(final EventBus eventBus, final MyView view, final MyProxy proxy) {
        super(eventBus, view, proxy, RevealType.Root);
    }
}

