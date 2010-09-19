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

/**
 * 
 * @author Sunny Gupta
 *
 * @param <A>
 * @param <R>
 */
public abstract class AbstractCachingClientActionHandler<A extends Action<R>, R extends Result> 
    extends AbstractClientActionHandler<A, R> {

  protected final Cache cache;

  public AbstractCachingClientActionHandler(Class<A> actionType, Cache cache) {
    super(actionType);
    this.cache = cache;
  }
  
  public void execute(final A action, final AsyncCallback<R> resultCallback, 
      final ClientDispatchRequest request, ExecuteCommand<A,R> executeCommand) {
    // Prefetch to see if result is cached
    R prefetchResult = this.prefetch(action);
    if (prefetchResult != null) {
      // Return the cached result
      resultCallback.onSuccess(prefetchResult);
    } else {
      // Execute
      executeCommand.execute(action, new AsyncCallback<R>() {

        @Override
        public void onFailure(Throwable caught) {
          resultCallback.onFailure(caught);
        }

        @Override
        public void onSuccess(R result) {
          if (!request.isCancelled()) {
            // Postfetch
            postfetch(action, result);
            
            // Callback
            resultCallback.onSuccess(result);
          }
        }
        
      });
    }
  };

  public abstract R prefetch(A action);
  
  public abstract void postfetch(A action, R result);

}
