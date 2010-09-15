/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.client.actionhandler;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.gwtplatform.dispatch.client.ClientDispatchRequest;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Instances of this interface will handle specific types of {@link Action}
 * classes on the client.
 * <p/>
 * When a command is executed (or undone), {@link ClientActionHandler}s that
 * have been registered with the bound {@link ClientActionHandlerRegistry} will
 * be called instead of {@link DispatchAsync} sending the command over gwt-rpc
 * to the server.
 * 
 * Client Action Handlers provide a number of flexible options:
 * <ul>
 * <li>The action can be modified before sending the action over gwt-rpc to the
 * server.</li>
 * <li>A result can be returned without contacting the server.</li>
 * <li>The result can be modified after it is returned from the server.</li>
 * <li>Before or after returning the result to the calling code, the result can
 * be processed.</li>
 * <li>An alternate way of communicating with a server could be used instead of
 * gwt-rpc.</li>
 * </ul>
 * <p/>
 * <b>Important!</b> Your action handlers must be thread safe since they will be
 * singletons.
 * 
 * @param <A> The type of the action extending {@link Action}.
 * @param <R> The type of the result extending {@link Result}.
 * 
 * @author Brendan Doherty
 */
public interface ClientActionHandler<A extends Action<R>, R extends Result> {

  /**
   * Handles the specified action.
   * 
   * @param action The action to handle.
   * @param resultCallback A callback that can be used both return the result,
   *          or return any exceptions that are caught.
   * @param request An instance of
   *          {@link com.gwtplatform.dispatch.client.DispatchRequest
   *          DispatchRequest} that will been returned to the calling code that
   *          requested the action be executed. It is possible that the calling
   *          code may cancel the request, so after returning from asynchronous
   *          calls, confirm that {@link ClientDispatchRequest#isCancelled()} is
   *          still <code>false</code>. Also, if you get an instance of
   *          {@link com.google.gwt.http.client.Request Request} as a result of
   *          network communications, call
   *          {@link ClientDispatchRequest#setRequest()} so that the calling
   *          code has the ability to cancel the http request.
   * @param dispatch A callback that provides a way to send the action (possibly
   *          modified) to the server over gwt-rpc. If you want to either modify
   *          or process the result, create a new <code>AsyncCallback</code>
   *          object, otherwise just pass in <code>resultCallback</code>.
   */
  void execute(A action, AsyncCallback<R> resultCallback,
      ClientDispatchRequest request, AsyncExecute<A, R> dispatch);

  /**
   * @return The type of {@link Action} supported by this handler.
   */
  Class<A> getActionType();

  /**
   * Undoes the specified action.
   * 
   * @param action The action to undo.
   * @param result The result to undo.
   * @param callback A callback that can be both used to indicate that the undo
   *          is complete, or return any exceptions that are caught.
   * @param request An instance of
   *          {@link com.gwtplatform.dispatch.client.DispatchRequest
   *          DispatchRequest} that will been returned to the calling code that
   *          requested the action be executed. It is possible that the calling
   *          code may cancel the request, so after returning from asynchronous
   *          calls, confirm that {@link ClientDispatchRequest#isCancelled()} is
   *          still <code>false</code>. Also, if you get an instance of
   *          {@link com.google.gwt.http.client.Request Request} as a result of
   *          network communications, call
   *          {@link ClientDispatchRequest#setRequest()} so that the calling
   *          code has the ability to cancel the http request.
   * @param dispatch A callback that provides a way to send the action (possibly
   *          modified) to the server over gwt-rpc. If you want to perform
   *          additional processing after the server has returned, create a new
   *          <code>AsyncCallback</code> object, otherwise just pass in
   *          <code>callback</code>.
   */
  void undo(A action, R result, AsyncCallback<Void> callback,
      ClientDispatchRequest request, AsyncUndo<A, R> dispatch);

}
