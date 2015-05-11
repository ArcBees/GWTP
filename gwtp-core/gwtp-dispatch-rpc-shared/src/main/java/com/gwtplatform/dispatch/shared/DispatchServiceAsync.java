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

package com.gwtplatform.dispatch.shared;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous client-side dispatcher service. The server-side
 * implementation is {@link com.gwtplatform.dispatch.server.guice.DispatchServiceImpl}
 * .
 * <p/>
 * This class is closely related to {@link DispatchAsync}, in theory the latter
 * wouldn't be needed, but we use it to workaround a GWT limitation described in
 * {@link com.gwtplatform.dispatch.client.DispatchAsync}.
 *
 * @see com.gwtplatform.dispatch.client.DispatchAsync
 * @see com.gwtplatform.dispatch.server.Dispatch
 * @see com.gwtplatform.dispatch.server.guice.DispatchImpl
 * @see com.gwtplatform.dispatch.shared.DispatchService
 * @see com.gwtplatform.dispatch.shared.DispatchServiceAsync
 * @see com.gwtplatform.dispatch.server.guice.DispatchServiceImpl
 *
 * @deprecated Please use {@link com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync}.
 */
@Deprecated
public interface DispatchServiceAsync {
    /**
     * This method is called client-side whenever a new action is executed.
     *
     * @see DispatchService#execute
     */
    Request execute(String cookieSentByRPC, Action<?> action,
                    AsyncCallback<Result> callback);

    /**
     * This method is called client-side whenever a previous executed action need
     * to be undone.
     *
     * @see DispatchService#undo
     */
    Request undo(String cookieSentByRPC, Action<?> action, Result result,
                 AsyncCallback<Void> callback);
}
