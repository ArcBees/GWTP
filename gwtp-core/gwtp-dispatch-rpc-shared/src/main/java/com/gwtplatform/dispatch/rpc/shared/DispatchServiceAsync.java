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

package com.gwtplatform.dispatch.rpc.shared;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous client-side dispatcher service. The server-side implementation is
 * {@code com.gwtplatform.dispatch.rpc.server.guice.DispatchServiceImpl}.
 * <p>
 * This class is closely related to {@link com.gwtplatform.dispatch.rpc.shared.DispatchAsync}, in theory the latter
 * wouldn't be needed, but we use it to workaround a GWT limitation described in
 * {@code com.gwtplatform.dispatch.rpc.client.DispatchAsync}.
 *
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchService
 * @see DispatchServiceAsync
 */
public interface DispatchServiceAsync {
    /**
     * This method is called client-side whenever a new action is executed.
     *
     * @see com.gwtplatform.dispatch.rpc.shared.DispatchService#execute
     */
    <R extends Result> Request execute(String cookieSentByRPC, Action<R> action,
            AsyncCallback<R> callback);

    /**
     * This method is called client-side whenever a previous executed action need to be undone.
     *
     * @see com.gwtplatform.dispatch.rpc.shared.DispatchService#undo
     */
    <R extends Result> Request undo(String cookieSentByRPC, Action<R> action, R result,
            AsyncCallback<Void> callback);
}
