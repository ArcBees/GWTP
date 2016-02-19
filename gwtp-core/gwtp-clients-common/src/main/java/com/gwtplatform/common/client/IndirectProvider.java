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

/**
 * Act as a {@link com.google.inject.Provider Provider}, but instead of returning the provided object, the user must
 * pass a callback in which the object is handled. This makes it possible to use the {@link com.google.inject.Provider
 * Provider} for code-splitting. This feature should be available in a later release of GIN. See <a
 * href="http://code.google.com/p/google-gin/issues/detail?id=61">GIN Issue 61</a> for details.
 *
 * @param <T> The type of the provided object.
 */
public interface IndirectProvider<T> {
    /**
     * Asynchronously get the provided object.
     *
     * @param callback The {@link AsyncCallback} to invoke once the object is available. The parameter to the callback
     * will be the provided object.
     */
    void get(AsyncCallback<T> callback);
}
