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
 * Abstract base class for Client side Action Handlers with Caching Support.
 * <p>Currently supported features include:</p>
 * <p>1. Prefetch / Postfetch to actually do the Cache lookup, which provides flexibility of changing the caching logic</p>
 * <p>2. Same Action Queuing to optimize on number of server trips</p>
 * <p>3. Flexibility of Cache implementation to support custom Caching</p>
 * 
 * @author Sunny Gupta
 *
 * @param <A> The type of the action extending {@link Action}.
 * @param <R> The type of the result extending {@link Result}.
 */
public abstract class AbstractCachingClientActionHandler<A extends Action<R>, R extends Result> 
    extends AbstractClientActionHandler<A, R> {

  private final Cache cache;
  
  // Holds callbacks, so that for multiple requests before the first returns (is served), we save round trips as well
  /* TODO Generics cause null type for result...not using generics forces typecasting...
   * TODO Cannot use A, R as this is static field
   * TODO Something is wrong somewhere?
   * private static HashMap<Action<? extends Result>, ArrayList<AsyncCallback<? extends Result>>> 
     pendingCallbackMap = new HashMap<Action<? extends Result>, ArrayList<AsyncCallback<? extends Result>>>();
   */  
  private static HashMap<Action<Result>, ArrayList<AsyncCallback<Result>>> 
      pendingCallbackMap = new HashMap<Action<Result>, ArrayList<AsyncCallback<Result>>>();

  public AbstractCachingClientActionHandler(Class<A> actionType, Cache cache) {
    super(actionType);
    this.cache = cache;
  }
  
  public void execute(final A action, final AsyncCallback<R> resultCallback, 
      final ClientDispatchRequest request, ExecuteCommand<A,R> executeCommand) {
    // First check if any pending callbacks for this action
    ArrayList<AsyncCallback<Result>> pendingCallbacks = pendingCallbackMap.get(action);
 
    // TODO Think about some timeout mechanism in case the pending call never returns. 
    // TODO Is such a scenario possible in the first place?
    if (pendingCallbacks != null) {
      // Add callback to pending list and return
      // TODO Try to get rid of unchecked cast,
      // TODO though actually it is not unchecked since R is bounded by Result
      pendingCallbacks.add((AsyncCallback<Result>) resultCallback);
      return;
    }
    
    // Prefetch to see if result is cached
    R prefetchResult = prefetch(action);
    if (prefetchResult != null) {
      // Return the cached result
      resultCallback.onSuccess(prefetchResult);
    } else {
      // Add pending callback
      ArrayList<AsyncCallback<Result>> resultCallbacks = new ArrayList<AsyncCallback<Result>>();
      // TODO Try to get rid of unchecked cast
      // TODO though actually it is not unchecked since A, R is bounded by Action, Result
      resultCallbacks.add((AsyncCallback<Result>) resultCallback);
      pendingCallbackMap.put((Action<Result>) action, resultCallbacks);
      
      // Execute
      executeCommand.execute(action, new AsyncCallback<R>() {

        @Override
        public void onFailure(Throwable caught) {
          // Call postfetch with null result
          postfetch(action, null);
          
          // Callback onFailure
          ArrayList<AsyncCallback<Result>> finishedCallbacks = pendingCallbackMap.remove(action);
          for (AsyncCallback<? extends Result> finishedCallback : finishedCallbacks) {
            finishedCallback.onFailure(caught);
          }
        }

        @Override
        public void onSuccess(R result) {
          if (!request.isCancelled()) {
            // Postfetch
            postfetch(action, result);
            
            // Callback onSuccess
            ArrayList<AsyncCallback<Result>> finishedCallbacks = pendingCallbackMap.remove(action);
            for (AsyncCallback<Result> finishedCallback : finishedCallbacks) {
              finishedCallback.onSuccess(result);
            }
          }
        }
        
      });
    }
  };

  public abstract R prefetch(A action);
  
  public abstract void postfetch(A action, R result);

  /**
   * @return the cache
   */
  public Cache getCache() {
    return cache;
  }

}
