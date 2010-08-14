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

package com.gwtplatform.dispatch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * The asynchronous client-side dispatcher service. The server-side implementation is {@link com.gwtplatform.dispatch.server.DispatchServiceImpl}.
 * <p />
 * This class is closely related to {@link DispatchAsync}, in theory the latter wouldn't
 * be needed, but we use it to workaround a GWT limitation described in {@link com.gwtplatform.dispatch.client.DispatchAsync}.
 * 
 * @see com.gwtplatform.dispatch.client.DispatchAsync
 * @see com.gwtplatform.dispatch.server.Dispatch
 * @see com.gwtplatform.dispatch.server.DispatchImpl
 * @see com.gwtplatform.dispatch.client.DispatchService
 * @see com.gwtplatform.dispatch.client.DispatchServiceAsync
 * @see com.gwtplatform.dispatch.server.DispatchServiceImpl
 * 
 * @author Philippe Beaudoin
 */
public interface DispatchServiceAsync {
  /**
   * This method is called client-side whenever a new action is executed.
   *
   * @see DispatchService#execute
   */
  com.google.gwt.http.client.Request execute( String cookieSentByRPC, Action<?> action, AsyncCallback<Result> callback );

  /**
   * This method is called client-side whenever a previous executed action need to be undone.
   *
   * @see DispatchService#undo
   */
  com.google.gwt.http.client.Request undo( String cookieSentByRPC, Action<?> action, Result result, AsyncCallback<Void> callback );
}
