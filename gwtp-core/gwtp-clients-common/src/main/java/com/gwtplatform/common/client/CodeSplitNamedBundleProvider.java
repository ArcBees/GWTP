/*
 * Copyright 2016 ArcBees Inc.
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

public class CodeSplitNamedBundleProvider<T, B extends NamedProviderBundle> implements IndirectProvider<T> {
    private final AsyncProvider<B> bundleProvider;
    private final String providerName;

    public CodeSplitNamedBundleProvider(
            AsyncProvider<B> bundleProvider,
            String providerName) {
        this.bundleProvider = bundleProvider;
        this.providerName = providerName;
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
                Provider<T> provider = (Provider<T>) providerBundle.get(providerName);
                callback.onSuccess(provider.get());
            }
        });
    }
}
