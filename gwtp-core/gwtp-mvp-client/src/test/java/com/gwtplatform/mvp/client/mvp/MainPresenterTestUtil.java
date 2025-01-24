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

package com.gwtplatform.mvp.client.mvp;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * This is the test presenter.
 */
public class MainPresenterTestUtil extends Presenter<MainPresenterTestUtil.MyView, MainPresenterTestUtil.MyProxy> {

    public static final SingleSlot<PresenterWidget<?>> SLOT_SetMainContent = new SingleSlot<PresenterWidget<?>>();

    /**
     * Presenter's view.
     */
    public interface MyView extends View {
    }

    /**
     * Presenter's proxy.
     */
    @ProxyStandard
    public interface MyProxy extends Proxy<MainPresenterTestUtil> {
    }

    private final PresenterWidget<?> subPresenter;

    @Inject
    public MainPresenterTestUtil(final EventBus eventBus, final MyView view,
            final MyProxy proxy, @Named("Sub") PresenterWidget<View> subPresenter) {
        super(eventBus, view, proxy, RevealType.Root);
        this.subPresenter = subPresenter;
    }

    public void setSubPresenter() {
        setInSlot(SLOT_SetMainContent, subPresenter);
    }
}

