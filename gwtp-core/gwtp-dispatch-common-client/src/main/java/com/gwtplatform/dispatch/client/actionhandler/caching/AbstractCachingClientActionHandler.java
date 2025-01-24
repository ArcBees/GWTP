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

package com.gwtplatform.dispatch.client.actionhandler.caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.CallbackDispatchRequest;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.DefaultCallbackDispatchRequest;
import com.gwtplatform.dispatch.client.DelegatingCallbackDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.AbstractClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.client.actionhandler.UndoCommand;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * Abstract base class for client-side action handlers with caching support.
 * <p>
 * Supported features include:
 * </p>
 * <ol>
 * <li>
 * {@link #prefetch}/{@link #postfetch} perform the cache lookup and the cache store. You can use this to customize
 * the caching logic.
 * </li>
 * <li>
 * Automatic action queuing so that calls in quick succession result in a single trip to the server.
 * </li>
 * <li>
 * Flexibility of cache implementation to support custom caching
 * </li>
 * </ol>
 * @deprecated Since 1.4.
 * Use com.gwtplatform.dispatch.rpc.client.interceptor.caching.AbstractCachingRpcInterceptor
 *
 * @param <A> The type of the action.
 * @param <R> The type of the result.
 */
@Deprecated
public abstract class AbstractCachingClientActionHandler<A, R> extends AbstractClientActionHandler<A, R> {
    private final Cache cache;

    // Holds callbacks, so that for multiple requests before the first returns (is served), we save round trips as well
    private final Map<A, List<CallbackDispatchRequest<R>>> pendingRequestCallbackMap =
            new HashMap<A, List<CallbackDispatchRequest<R>>>();

    public AbstractCachingClientActionHandler(Class<A> actionType, Cache cache) {
        super(actionType);
        this.cache = cache;
    }

    public DispatchRequest execute(final A action,
                                   final AsyncCallback<R> resultCallback, ExecuteCommand<A, R> executeCommand) {
        // First check if any pending callbacks for this action
        List<CallbackDispatchRequest<R>> pendingRequestCallbacks = pendingRequestCallbackMap.get(action);

        if (pendingRequestCallbacks != null) {
            CallbackDispatchRequest<R> callbackDispatchRequest = new DefaultCallbackDispatchRequest<R>(resultCallback);

            // Add callback to pending list and return
            pendingRequestCallbacks.add(callbackDispatchRequest);

            return callbackDispatchRequest;
        }

        // Prefetch to see if result is cached
        R prefetchResult = prefetch(action);
        if (prefetchResult != null) {
            // Return the cached result
            resultCallback.onSuccess(prefetchResult);

            return new CompletedDispatchRequest();
        } else {
            // Execute
            DispatchRequest request = executeCommand.execute(action,
                    new AsyncCallback<R>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            // Call postfetch with null result
                            postfetch(action, null);
                            resultCallback.onFailure(caught);

                            // Callback onFailure
                            List<CallbackDispatchRequest<R>> pendingRequestCallbacks =
                                    pendingRequestCallbackMap.remove(action);
                            for (CallbackDispatchRequest<R> pendingRequestCallback : pendingRequestCallbacks) {
                                if (pendingRequestCallback.isPending()) {
                                    pendingRequestCallback.onFailure(caught);
                                }
                            }
                        }

                        @Override
                        public void onSuccess(R result) {
                            // Postfetch
                            postfetch(action, result);
                            resultCallback.onSuccess(result);

                            // Callback onSuccess
                            List<CallbackDispatchRequest<R>> pendingRequestCallbacks =
                                    pendingRequestCallbackMap.remove(action);
                            for (CallbackDispatchRequest<R> pendingRequestCallback : pendingRequestCallbacks) {
                                if (pendingRequestCallback.isPending()) {
                                    pendingRequestCallback.onSuccess(result);
                                }
                            }
                        }
                    });

            // Add pending callback
            ArrayList<CallbackDispatchRequest<R>> resultRequestCallbacks = new ArrayList<CallbackDispatchRequest<R>>();

            CallbackDispatchRequest<R> callbackDispatchRequest = new DelegatingCallbackDispatchRequest<R>(request,
                    resultCallback);
            resultRequestCallbacks.add(callbackDispatchRequest);

            pendingRequestCallbackMap.put(action, resultRequestCallbacks);

            return callbackDispatchRequest;
        }
    }

    @Override
    public DispatchRequest undo(A action, R result, AsyncCallback<Void> callback,
                                UndoCommand<A, R> undoCommand) {
        // Remove the cached entry
        getCache().remove(action);
        // Undo the previous action
        return undoCommand.undo(action, result, callback);
    }

    /**
     * Override this method to perform an action before the call is sent to the server. If the call returns a
     * non-{@code null} result then the action is never executed on the server and the returned value is used. If the
     * call returns {@code null} then the action is executed on the server.
     * <p>
     * You can use this method to fetch the {@code action} from the cache.
     *
     * @param action The action to be prefetched
     * @return The prefetched result. If not found, return {@code null}.
     */
    protected abstract R prefetch(A action);

    /**
     * Override this method to perform an action after the call to the server returns successfully or not. If the call
     * succeeded, the result will be passed, if it failed {@code null} will be passed in the {@code result} parameter.
     * <p>
     * You can use this method to add the result to cache, if it is {@code null} you should remove the {@code action}
     * from the cache.
     *
     * @param action The action that just finished execution on the server.
     * @param result The result after the server call, or {@code null} if the server call failed.
     */
    protected abstract void postfetch(A action, R result);

    /**
     * @return the cache
     */
    protected Cache getCache() {
        return cache;
    }

}
