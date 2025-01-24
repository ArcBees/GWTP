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

import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.mvp.client.Presenter;

/**
 * This is the unparameterized base interface for proxy. It is provided as a
 * work around since GIN/Guice cannot inject parameterized types. For most
 * purposes you should use {@link Proxy}.
 */
public interface ProxyRaw extends HasHandlers {

    /**
     * Get the associated {@link Presenter}. The presenter can only be obtained in
     * an asynchronous manner to support code splitting when needed. To access the
     * presenter, pass a callback.
     * <p>
     * The difference between this method and
     * {@link Proxy#getPresenter}
     * is that the latter one gets the specific parameterised {@link Presenter}
     * type.
     *
     * @param callback The callback in which the {@link Presenter} will be passed
     *                 as a parameter.
     */
    void getRawPresenter(NotifyingAsyncCallback<Presenter<?, ?>> callback);

}
