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
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * The interface that {@link ClientActionHandler}s use send the action to undo
 * the server over gwt-rpc.
 * 
 * @author Brendan Doherty
 * 
 * @param <A> The {@link Action} type.
 * @param <R> The {@link Result} type.
 */
public interface AsyncUndo<A extends Action<R>, R extends Result> {
  /**
   * Undo an action.
   * 
   * @param action The action to undo.
   * @param result The result of the action to undo.
   * @param callback A callback that will either indicate success or failure.
   */
  void undo(A action, R result, AsyncCallback<Void> callback);
}
