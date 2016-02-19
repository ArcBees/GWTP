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

/**
 * An {@link IndirectProvider} that gets the object using code splitting and invokes the callback once the code is
 * loaded. This is essentially the same as a standard {@link AsyncProvider}, but shares the interface of other {@link
 * IndirectProvider}.
 *
 * @param <T> The type of the provided object.
 */
public final class CodeSplitProvider<T> implements IndirectProvider<T> {
    private final AsyncProvider<T> provider;

    /**
     * Construct an {@link IndirectProvider} that gets the object using code splitting and invokes the callback once the
     * code is loaded.
     *
     * @param provider The {@link AsyncProvider} providing the object.
     */
    public CodeSplitProvider(AsyncProvider<T> provider) {
        this.provider = provider;
    }

    @Override
    public void get(AsyncCallback<T> callback) {
        provider.get(callback);
    }
}
