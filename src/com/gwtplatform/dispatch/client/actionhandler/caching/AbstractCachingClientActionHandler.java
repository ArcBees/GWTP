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

package com.gwtplatform.dispatch.client.actionhandler.caching;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.gwtplatform.dispatch.client.ClientDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.AbstractClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract base class for client-side action handlers with caching support.
 * <p>Supported features include:</p>
 * <p>1. {@link #prefetch}/{@link #postfetch} perform the cache lookup and the 
 * cache store. You can use this to customize the caching logic.</p>
 * <p>2. Automatic action queuing so that calls in quick succession result in a 
 * single trip to the server.</p>
 * <p>3. Flexibility of cache implementation to support custom caching</p>
 * 
 * @author Sunny Gupta
 *
 * @param <A> The type of the action extending {@link Action}.
 * @param <R> The type of the result extending {@link Result}.
 */
public abstract class AbstractCachingClientActionHandler<A extends Action<R>, R extends Result> 
    extends AbstractClientActionHandler<A, R> {

  /**
   * Request-Callback pair to hold them together and make callbacks based on the request status.
   * 
   * @author Sunny Gupta
   *
   */
  private class RequestCallbackPair {
    private final ClientDispatchRequest request;
    private final AsyncCallback<R> callback;
    
    public RequestCallbackPair(final ClientDispatchRequest request, final AsyncCallback<R> callback) {
      this.request = request;
      this.callback = callback;
    }
    
    public AsyncCallback<R> getCallback() {
      return callback;
    }
    public ClientDispatchRequest getRequest() {
      return request;
    }
  }
  
  private final Cache cache;
  
  // Holds callbacks, so that for multiple requests before the first returns (is served), we save round trips as well
  private HashMap<A, ArrayList<RequestCallbackPair>> 
      pendingRequestCallbackMap = new HashMap<A, ArrayList<RequestCallbackPair>>();
  
  public AbstractCachingClientActionHandler(Class<A> actionType, Cache cache) {
    super(actionType);
    this.cache = cache;
  }
  
  public void execute(final A action, final AsyncCallback<R> resultCallback, 
      final ClientDispatchRequest request, ExecuteCommand<A,R> executeCommand) {
    // First check if any pending callbacks for this action
    ArrayList<RequestCallbackPair> pendingRequestCallbacks = pendingRequestCallbackMap.get(action);
 
    if (pendingRequestCallbacks != null) {
      // Add callback to pending list and return
      pendingRequestCallbacks.add(new RequestCallbackPair(request, resultCallback));
      return;
    }
    
    // Prefetch to see if result is cached
    R prefetchResult = prefetch(action);
    if (prefetchResult != null) {
      // Return the cached result
      resultCallback.onSuccess(prefetchResult);
    } else {
      // Add pending callback
      ArrayList<RequestCallbackPair> resultRequestCallbacks = new ArrayList<RequestCallbackPair>();
      resultRequestCallbacks.add(new RequestCallbackPair(request, resultCallback));
      pendingRequestCallbackMap.put(action, resultRequestCallbacks);
      
      // Execute
      executeCommand.execute(action, new AsyncCallback<R>() {

        @Override
        public void onFailure(Throwable caught) {
          // Call postfetch with null result
          postfetch(action, null);
          
          // Callback onFailure
          ArrayList<RequestCallbackPair> pendingRequestCallbacks = pendingRequestCallbackMap.remove(action);
          for (RequestCallbackPair pendingRequestCallback : pendingRequestCallbacks) {
            // TODO Do we also gate call to onFailure with request cancellation?
            if (!pendingRequestCallback.getRequest().isCancelled()) {
              pendingRequestCallback.getCallback().onFailure(caught);
            }
          }
        }

        @Override
        public void onSuccess(R result) {
          // Postfetch
          postfetch(action, result);

          // Callback onSuccess
          ArrayList<RequestCallbackPair> pendingRequestCallbacks = pendingRequestCallbackMap.remove(action);
          for (RequestCallbackPair pendingRequestCallback : pendingRequestCallbacks) {
            if (!pendingRequestCallback.getRequest().isCancelled()) {
              pendingRequestCallback.getCallback().onSuccess(result);
            }
          }
        }
        
      });
    }
  };

  /**
   * Override this method to perform prefetching from the cache to see if the result is available.
   * 
   * @param action The action to be prefetched
   * @return The prefetched result. If not found, return null
   */
  public abstract R prefetch(A action);
  
  /**
   * Override this method to perform postfetch after the server trip returns. The result will be empty if
   * the server call failed. You can add the result to the cache here based on specific requirements so that
   * the results can be prefetched in subsequent calls.
   * 
   * @param action The action to be postfetched
   * @param result The result after the server call
   */
  public abstract void postfetch(A action, R result);

  /**
   * @return the cache
   */
  public Cache getCache() {
    return cache;
  }

}
