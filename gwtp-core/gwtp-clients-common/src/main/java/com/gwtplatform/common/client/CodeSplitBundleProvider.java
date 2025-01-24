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

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;

import jakarta.inject.Provider;

/**
 * Implements a {@link IndirectProvider} that uses code splitting for a specific type. The object will be provided from
 * a {@link ProviderBundle}.
 *
 * @param <T> The type of the provided object.
 * @param <B> The type of the {@link ProviderBundle} providing this object.
 */
public class CodeSplitBundleProvider<T, B extends ProviderBundle> implements IndirectProvider<T> {
    private final AsyncProvider<B> bundleProvider;
    private final int providerId;

    /**
     * Construct a {@link IndirectProvider} that implements code splitting for a specific type. The object will be
     * provided from a {@link ProviderBundle}.
     *
     * @param bundleProvider The {@link ProviderBundle} providing the object.
     * @param providerId The identifier of the provided object, within the {@link ProviderBundle}.
     */
    public CodeSplitBundleProvider(AsyncProvider<B> bundleProvider, int providerId) {
        this.bundleProvider = bundleProvider;
        this.providerId = providerId;
    }

    @Override
    public void get(final AsyncCallback<T> callback) {
        bundleProvider.get(new AsyncCallback<B>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(B providerBundle) {
                callback.onSuccess(((Provider<T>) providerBundle.get(providerId)).get());
            }
        });
    }
}
