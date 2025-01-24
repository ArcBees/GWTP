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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.mvp.client.Presenter;

/**
 * This implementation of {@link AsyncCallback} can be used when fetching data from the server within {@link
 * Presenter#prepareFromRequest(com.gwtplatform.mvp.shared.proxy.PlaceRequest)} for a presenter that uses manual reveal
 * (see {@link Presenter#useManualReveal()}.
 * <p>
 * Use {@link #create(Presenter, AsyncCallback)} to attach that callback to your own.
 * <p>
 * For more complex scenarios you can use {@link ProxyPlace#manualReveal(Presenter)}.
 *
 * @param <T> The type of the return value, see {@link AsyncCallback}.
 */
public class ManualRevealCallback<T> implements AsyncCallback<T> {
    private final Presenter<?, ? extends ProxyPlace<?>> presenter;
    private final AsyncCallback<T> callback;

    /**
     * Creates an {@link ManualRevealCallback} that is not attached to another {@link AsyncCallback}.
     *
     * @param presenter The presenter that will be revealed upon successful completion of this callback.
     *
     * @see #ManualRevealCallback(Presenter, AsyncCallback)
     */
    public ManualRevealCallback(Presenter<?, ? extends ProxyPlace<?>> presenter) {
        this.presenter = presenter;
        this.callback = null;
    }

    ManualRevealCallback(
            Presenter<?, ? extends ProxyPlace<?>> presenter,
            AsyncCallback<T> callback) {
        this.presenter = presenter;
        this.callback = callback;
    }

    /**
     * Creates an {@link ManualRevealCallback} that is attached to another {@link AsyncCallback}.
     *
     * @param presenter The presenter that will be revealed upon successful completion of this callback.
     *
     * @see #ManualRevealCallback(Presenter, AsyncCallback)
     */
    public static <T> ManualRevealCallback<T> create(Presenter<?, ? extends ProxyPlace<?>> presenter,
            AsyncCallback<T> callback) {
        return new ManualRevealCallback<>(presenter, callback);
    }

    @Override
    public void onFailure(Throwable caught) {
        presenter.getProxy().manualRevealFailed();
        if (callback != null) {
            callback.onFailure(caught);
        }
    }

    @Override
    public void onSuccess(T result) {
        if (callback != null) {
            callback.onSuccess(result);
        }
        presenter.getProxy().manualReveal(presenter);
    }
}
