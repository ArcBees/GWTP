/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * An asynchronous dispatcher service with an arbitrary action type. The actions will be sent over HTTP using a
 * REST architecture.
 */
public interface RestDispatch {
    /**
     * This method is called client-side whenever a new action is executed.
     *
     * @param action   the {@link com.gwtplatform.dispatch.rest.shared.RestAction} to execute.
     * @param callback the {@link AsyncCallback} to call when the execution is done.
     * @see com.gwtplatform.dispatch.rest.client.RestDispatchCall#execute()
     */
    <A extends RestAction<R>, R> DispatchRequest execute(A action, AsyncCallback<R> callback);
}
