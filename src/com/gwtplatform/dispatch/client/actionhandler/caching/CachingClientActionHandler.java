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
import com.gwtplatform.dispatch.client.actionhandler.UndoCommand;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * 
 * @author Sunny Gupta
 *
 * @param <A>
 * @param <R>
 */
public class CachingClientActionHandler<A extends Action<R>, R extends Result> extends
    AbstractCachingClientActionHandler<A, R> {
  
  public CachingClientActionHandler(Class<A> actionType, Cache cache) {
    super(actionType, cache);
  }

  @Override
  public void postfetch(A action, R result) {
    // Check if null result
    if (result == null) {
      getCache().remove(action);
    } else {
      // Cache
      getCache().put(action, result);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public R prefetch(A action) {
    try {
      // Check if Action available in Cache
      Object value = super.getCache().get(action);
      if (value != null && value instanceof Result) {
        return (R) value;
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public void undo(A action, R result, AsyncCallback<Void> callback,
      ClientDispatchRequest request, UndoCommand<A, R> undoCommand) {
    // Remove the cached entry
    getCache().remove(action);
    // Undo the previous action
    undoCommand.undo(action, result, callback);
  }

}
