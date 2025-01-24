/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.client;

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.ExceptionHandler.Status;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * An class representing a call made to the server through com.gwtplatform.dispatch.rest.client.RestDispatch
 * or com.gwtplatform.dispatch.rpc.shared.DispatchAsync.
 * <p>
 * This class will perform the work shared by all dispatch modules. It will delegate exceptions to the bound {@link
 * ExceptionHandler}. It will provide access to the security cookie through the bound {@link SecurityCookieAccessor}.
 * <p>
 * It also provides a couple extension points for implementations.
 *
 * @param <A> The type of the {@link TypedAction} wrapped by this {@link DispatchCall}.
 * @param <R> The type of the result of the wrapped {@link TypedAction}.
 */
public abstract class DispatchCall<A extends TypedAction<R>, R> {
    private final A action;
    private final ExceptionHandler exceptionHandler;
    private final SecurityCookieAccessor securityCookieAccessor;
    private final AsyncCallback<R> callback;

    private boolean intercepted;

    protected DispatchCall(
            ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            A action,
            AsyncCallback<R> callback) {
        this.action = action;
        this.callback = callback;
        this.exceptionHandler = exceptionHandler;
        this.securityCookieAccessor = securityCookieAccessor;
    }

    public void setIntercepted(boolean intercepted) {
        if (this.intercepted && !intercepted) {
            throw new IllegalStateException("Can not overwrite the intercepted state of a DispatchCall.");
        }

        this.intercepted = intercepted;
    }

    public boolean isIntercepted() {
        return intercepted;
    }

    /**
     * Execution entry point. Call this method to execute the {@link TypedAction action} wrapped by this instance.
     *
     * @return a {@link DispatchRequest} object.
     */
    public abstract DispatchRequest execute();

    /**
     * Direct execution of a dispatch call without intercepting. Implementations must override this method to perform
     * additional work when {@link #execute()} is called.
     *
     * @return a {@link DispatchRequest} object.
     */
    protected abstract DispatchRequest processCall();

    /**
     * Returns the {@link TypedAction} wrapped by this {@link DispatchCall}.
     *
     * @return the {@link TypedAction} wrapped by this object.
     */
    protected A getAction() {
        return action;
    }

    /**
     * The {@link AsyncCallback} to use when the execution of the action wrapped by this object is completed.
     *
     * @return the {@link AsyncCallback} to call when the action has been executed.
     */
    protected AsyncCallback<R> getCallback() {
        return callback;
    }

    /**
     * Returns the bound {@link ExceptionHandler}.
     *
     * @return the bound {@link ExceptionHandler}.
     */
    protected ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Returns the bound {@link SecurityCookieAccessor}.
     *
     * @return the bound {@link SecurityCookieAccessor}.
     */
    protected SecurityCookieAccessor getSecurityCookieAccessor() {
        return securityCookieAccessor;
    }

    /**
     * Returns the current security cookie as returned by the bound {@link SecurityCookieAccessor}.
     *
     * @return the current security cookie.
     */
    protected String getSecurityCookie() {
        return securityCookieAccessor.getCookieContent();
    }

    /**
     * Override this method to perform additional work when the action execution succeeded.
     *
     * @param result the action result.
     */
    protected void onExecuteSuccess(R result) {
        callback.onSuccess(result);
    }

    /**
     * Override this method to perform additional work when the action execution succeeded.
     *
     * @param result the action result.
     * @param response the action {@link Response}.
     */
    protected void onExecuteSuccess(R result, Response response) {
        onExecuteSuccess(result);
    }

    /**
     * Override this method to perform additional work when the action execution failed.
     *
     * @param caught the caught {@link Throwable}.
     */
    protected void onExecuteFailure(Throwable caught) {
        if (exceptionHandler != null && exceptionHandler.onFailure(caught) == Status.STOP) {
            return;
        }

        callback.onFailure(caught);
    }

    /**
     * Override this method to perform additional work when the action execution failed.
     *
     * @param caught the caught {@link Throwable}.
     * @param response the failure {@link Response}.
     */
    protected void onExecuteFailure(Throwable caught, Response response) {
        onExecuteFailure(caught);
    }
}
