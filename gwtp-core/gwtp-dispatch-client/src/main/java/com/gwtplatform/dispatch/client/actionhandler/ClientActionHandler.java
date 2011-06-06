/**
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

package com.gwtplatform.dispatch.client.actionhandler;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Instances of this interface will handle specific types of {@link Action}
 * classes on the client.
 * <p/>
 * When a command is executed (or undone), the {@link ClientActionHandler} that
 * has been registered with the bound {@link ClientActionHandlerRegistry} is
 * called and {@link com.gwtplatform.dispatch.shared.DispatchAsync DispatchAsync} does
 * not automatically send the command over gwt-rpc to the server.
 *
 * Client Action Handlers provide a number of flexible options:
 * <ul>
 * <li>The action can be modified before sending the action over gwt-rpc to the
 * server.</li>
 * <li>A result can be returned without contacting the server.</li>
 * <li>The result can be modified or processed after it is returned from the
 * server.</li>
 * <li>The {@link ClientActionHandler} can take over and communicate directly
 * with the server, possibly using a different mechanism than gwt-rpc.</li>
 * </ul>
 * <p/>
 *
 * <b>Important!</b> If your action handler makes asynchronous calls, be careful
 * with your use of fields as a second call your handler could be made while it
 * is waiting for the asynchronous call to return.
 *
 * <h3>Caching Client Action Handler Example</h3>
 *
 * <pre>
 * <code>
 * // Interface of cache singleton
 * public interface Cache {
 *   &lt;A extends Action&lt;R&gt;, R extends Result&gt; R get(A action);
 *   &lt;A extends Action&lt;R&gt;, R extends Result&gt; void put(A action, R result);
 * }
 *
 * // Client action handler that injects the cache
 * public class RetrieveFooClientActionHandler
 *     extends
 *     AbstractCachingClientActionHandler&lt;RetrieveFooAction, RetrieveFooResult&gt; {
 *   {@literal}@Inject
 *   RetrieveFooClientActionHandler(
 *       Cache cache) {
 *
 *     super(RetrieveFooAction.class, cache);
 *   }
 * }
 *
 * // abstract client action handler that:
 * // - first checks cache and returns result immediately if found in cache
 * // - executes command on server using gwt-rpc
 * // - saves result to cache before returning it
 * public abstract class AbstractCachingClientActionHandler&lt;A extends Action&lt;R&gt;, R extends Result&gt;
 *     extends AbstractClientActionHandler&lt;A, R&gt; {
 *
 *   private final Cache cache;
 *
 *   public AbstractCachingClientActionHandler(
 *       Class&lt;A&gt; actionType, Cache cache) {
 *
 *     super(actionType);
 *     this.cache = cache;
 *   }
 *
 *   {@literal}@Override
 *   public DispatchRequest execute(final A action, final AsyncCallback&lt;R&gt; resultCallback,
 *       ExecuteCommand&lt;A, R&gt; executeCommand) {
 *
 *     R cacheResult = cache.get(action);
 *     if (cacheResult != null) {
 *       resultCallback.onSuccess(cacheResult);
 *       return new CompletedDispatchRequest();
 *     } else {
 *       return executeCommand.execute(action, new AsyncCallback&lt;R&gt;() {
 *         {@literal}@Override
 *         public void onSuccess(R result) {
 *           if(!request.isCancelled()) {
 *             cache.put(action, result);
 *             resultCallback.onSuccess(result);
 *           }
 *         }
 *
 *         {@literal}@Override
 *         public void onFailure(Throwable caught) {
 *           resultCallback.onFailure(caught);
 *         }
 *       });
 *     }
 *   }
 *
 *   {@literal}@Override
 *   public DispatchRequest undo(A action, R result, AsyncCallback&lt;Void&gt; callback,
 *       ClientDispatchRequest request, UndoCommand&lt;A, R&gt; undoCommand) {
 *     // do nothing
 *     return new CompletedDispatchRequest();
 *   }
 * }
 * </code>
 * </pre>
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
   * If the handler makes asynchronous calls, it is recommended that you confirm
   * that this request has not been cancelled after returning by calling
   * {@link DelegatingDispatchRequest#isCancelled()} against the request
   * parameter.
   *
   * @param action The {@link Action} to execute.
   * @param resultCallback The callback to use to communicate the result of the
   *          action. Unless the request is cancelled, you must invoke
   *          {@link AsyncCallback#onSuccess} on this callback once you have
   *          obtained the result. If any failure occurs call
   *          {@link AsyncCallback#onFailure}.
   * @param executeCommand Call {@link ExecuteCommand#execute(Action, com.google.gwt.user.client.rpc.AsyncCallback)}
   *          on this object to send the action over to the server via gwt-rpc.
   *          As a parameter you can pass {@code resultCallback} or your custom
   *          {@link AsyncCallback} if you want to process the result.
   * @return A {@link DispatchRequest} object. Never return {@code null},
   *         instead return a new {@link com.gwtplatform.dispatch.client.CompletedDispatchRequest CompletedDispatchRequest}
   *         if you executed, cancelled or ignored the action.
   */
  DispatchRequest execute(A action, AsyncCallback<R> resultCallback,
      ExecuteCommand<A, R> executeCommand);

  /**
   * @return The type of {@link Action} supported by this handler.
   */
  Class<A> getActionType();

  /**
   * Undoes the specified action.
   *
   * If the handler makes asynchronous calls, it is recommended that you confirm
   * that this request has not been cancelled after returning by calling
   * {@link DelegatingDispatchRequest#isCancelled()} against the request
   * parameter.
   *
   * @param action The {@link Action} to undo.
   * @param result The {@link Result} to undo.
   * @param callback The callback to use to indicate when the action has been
   *          undone. Unless the request is cancelled, you must invoke
   *          {@link AsyncCallback#onSuccess} on this callback when you have
   *          successfully undone the action. If any failure occurs call
   *          {@link AsyncCallback#onFailure}.
   * @param undoCommand Call {@link UndoCommand#undo(Action, Result, com.google.gwt.user.client.rpc.AsyncCallback)} on
   *          this object to send the action over to the server via gwt-rpc. As
   *          a parameter you can pass {@code callback} or your custom
   *          {@link AsyncCallback} if you want to perform any processing
   *          following the undo.
   * @return A {@link DispatchRequest} object. Never return {@code null},
   *         instead return a new {@link com.gwtplatform.dispatch.client.CompletedDispatchRequest CompletedDispatchRequest}
   *         if you executed, cancelled or ignored the action.
   */
  DispatchRequest undo(A action, R result, AsyncCallback<Void> callback,
      UndoCommand<A, R> undoCommand);

}
