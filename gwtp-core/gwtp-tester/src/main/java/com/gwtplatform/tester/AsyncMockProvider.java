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

package com.gwtplatform.tester;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * For use in test cases where an {@link AsyncProvider} is required to provide an object and the test case needs to
 * provide a mock of the object.
 * <p>
 * Note that the same mock will be returned for every invocation of of {@link #get(AsyncCallback)} (it behaves as a
 * singleton) which may impact your tests, for example if you rely on {@code ==}.
 *
 * @param <T> The type of mock object provided
 */
public class AsyncMockProvider<T> implements AsyncProvider<T> {
    private final T mock;
    private final Throwable error;

    /**
     * Construct a {@link AsyncProvider} that will provide the mock object.
     *
     * @param mock The mock object to provide.
     */
    public AsyncMockProvider(T mock) {
        this.mock = mock;
        this.error = null;
    }

    /**
     * Construct a {@link AsyncProvider} that will fail to provide the mock object, and will fail with the specified
     * error.
     *
     * @param error The error to fail with, a {@link Throwable}.
     */
    public AsyncMockProvider(Throwable error) {
        this.mock = null;
        this.error = error;
    }

    @Override
    public void get(AsyncCallback<? super T> callback) {
        if (this.error != null) {
            callback.onFailure(error);
        } else {
            callback.onSuccess(this.mock);
        }
    }
}
