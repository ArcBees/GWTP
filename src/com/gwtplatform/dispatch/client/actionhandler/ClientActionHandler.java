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
 * /** Instances of this interface will handle specific types of {@link Action}
 * classes.
 * <p />
 * <b>Important!</b> Your action handlers must be thread safe since they will be
 * bound as singletons. For details, see <a href=
 * "http://code.google.com/p/google-guice/wiki/Scopes#Scopes_and_Concurrency">
 * http://code.google.com/p/google-guice/wiki/Scopes#Scopes_and_Concurrency</a>.
 * 
 * @param <A> The type of the action extending {@link Action}.
 * @param <R> The type of the result extending {@link Result}.
 * 
 * @author Brendan Doherty
 */
public interface ClientActionHandler<A extends Action<R>, R extends Result> {

  /**
   * FIXME
   */
  void execute(A action, AsyncCallback<R> resultCallback,
      AsyncExecute<A, R> dispatch);

  /**
   * @return The type of {@link Action} supported by this handler.
   */
  Class<A> getActionType();

  /**
   * FIXME
   */
  void undo(A action, R result, AsyncCallback<Void> callback,
      AsyncUndo<A, R> dispatch);

}
