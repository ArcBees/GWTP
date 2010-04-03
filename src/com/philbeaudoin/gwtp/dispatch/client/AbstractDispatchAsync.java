package com.philbeaudoin.gwtp.dispatch.client;

/**
 * Copyright 2010 Philippe Beaudoin
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


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * An abstract base class that provides methods that can be called to handle success or failure
 * results from the remote service. These should be called by the implementation of
 * {@link #execute(com.philbeaudoin.gwtp.dispatch.shared.Action, com.google.gwt.user.client.rpc.AsyncCallback)}.
 * 
 * @author David Peterson
 */
public abstract class AbstractDispatchAsync implements DispatchAsync {

    private final ExceptionHandler exceptionHandler;

    public AbstractDispatchAsync( ExceptionHandler exceptionHandler ) {
        this.exceptionHandler = exceptionHandler;
    }

    protected <A extends Action<R>, R extends Result> void onFailure( A action, Throwable caught, final AsyncCallback<R> callback ) {
        if ( exceptionHandler != null && exceptionHandler.onFailure( caught ) == ExceptionHandler.Status.STOP ) {
            return;
        }

        callback.onFailure( caught );
    }

    protected <A extends Action<R>, R extends Result> void onSuccess( A action, R result, final AsyncCallback<R> callback ) {
        callback.onSuccess( result );
    }

}
