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

package com.gwtplatform.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import jakarta.inject.Provider;

/**
 * An {@link IndirectProvider} that immediately gets the object and invokes the callback, without performing code
 * splitting. This is essentially the same as a standard {@link Provider}, but shares the interface of other {@link
 * IndirectProvider}.
 *
 * @param <T> The type of the provided object.
 */
public class StandardProvider<T> implements IndirectProvider<T> {
    private final Provider<T> provider;

    /**
     * Creates a {@link IndirectProvider} that directly gets the object and invokes the callback.
     *
     * @param provider The {@link Provider} of the object.
     */
    public StandardProvider(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public void get(AsyncCallback<T> callback) {
        callback.onSuccess(provider.get());
    }
}
