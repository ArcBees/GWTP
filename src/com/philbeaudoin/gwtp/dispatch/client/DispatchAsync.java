/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * The asynchronous client-side dispatcher service with an arbitrary action type. The server-side implementation is {@link com.philbeaudoin.gwtp.dispatch.server.DispatchImpl}.
 * <p />
 * This class is closely related to {@link DispatchServiceAsync}. In theory this class wouldn't
 * be needed, but we use it to workaround a GWT limitation. In fact, GWT currently can't correctly 
 * handle having generic method templates in method signatures 
 * (eg. <code>&lt;A&gt; A create( Class<A> type )</code>)
 * 
 * @see com.philbeaudoin.gwtp.dispatch.client.DispatchAsync
 * @see com.philbeaudoin.gwtp.dispatch.server.Dispatch
 * @see com.philbeaudoin.gwtp.dispatch.server.DispatchImpl
 * @see com.philbeaudoin.gwtp.dispatch.client.DispatchService
 * @see com.philbeaudoin.gwtp.dispatch.client.DispatchServiceAsync
 * @see com.philbeaudoin.gwtp.dispatch.server.DispatchServiceImpl
 * 
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public interface DispatchAsync {
    <A extends Action<R>, R extends Result> void execute( A action, AsyncCallback<R> callback );
}
