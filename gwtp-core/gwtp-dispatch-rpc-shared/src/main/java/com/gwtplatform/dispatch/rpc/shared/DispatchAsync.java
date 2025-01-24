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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * The asynchronous client-side dispatcher service with an arbitrary action type. The server-side implementation is
 * com.gwtplatform.dispatch.rpc.server.guice.DispatchImpl.
 * <p>
 * This class is closely related to {@link DispatchServiceAsync}. In theory this class wouldn't be needed, but we use it
 * to workaround a GWT limitation. In fact, GWT currently can't correctly handle having generic method templates in
 * method signatures (eg. <code>&lt;A&gt; A create( Class&lt;A&gt; type )</code>)
 *
 * @see DispatchAsync
 * @see DispatchService
 * @see DispatchServiceAsync
 */
public interface DispatchAsync {
    /**
     * This method is called client-side whenever a new action is executed.
     *
     * @see DispatchServiceAsync#execute
     */
    <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback);

    /**
     * This method is called client-side whenever a previous executed action need to be undone.
     *
     * @see DispatchServiceAsync#undo
     */
    <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result, AsyncCallback<Void> callback);
}
