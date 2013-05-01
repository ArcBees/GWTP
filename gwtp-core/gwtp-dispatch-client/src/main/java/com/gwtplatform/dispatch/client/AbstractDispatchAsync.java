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

package com.gwtplatform.dispatch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerMismatchException;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.client.actionhandler.UndoCommand;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

public abstract class AbstractDispatchAsync implements DispatchAsync {
    private final ClientActionHandlerRegistry clientActionHandlerRegistry;
    private final ExceptionHandler exceptionHandler;
    private final SecurityCookieAccessor securityCookieAccessor;

    public AbstractDispatchAsync(ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            ClientActionHandlerRegistry clientActionHandlerRegistry) {
        this.exceptionHandler = exceptionHandler;
        this.clientActionHandlerRegistry = clientActionHandlerRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(final A action,
            final AsyncCallback<R> callback) {
        prepareExecute(action);

        final String securityCookie = securityCookieAccessor.getCookieContent();

        final IndirectProvider<ClientActionHandler<?, ?>> clientActionHandlerProvider =
                clientActionHandlerRegistry.find(action.getClass());

        if (clientActionHandlerProvider != null) {
            final DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
            clientActionHandlerProvider.get(new AsyncCallback<ClientActionHandler<?, ?>>() {
                @Override
                public void onSuccess(ClientActionHandler<?, ?> clientActionHandler) {

                    if (clientActionHandler.getActionType() != action.getClass()) {
                        dispatchRequest.cancel();
                        callback.onFailure(new ClientActionHandlerMismatchException(
                                (Class<? extends Action<?>>) action.getClass(), clientActionHandler.getActionType()));
                        return;
                    }

                    if (dispatchRequest.isPending()) {
                        dispatchRequest.setDelegate(((ClientActionHandler<A, R>) clientActionHandler).execute(
                                action, callback, new ExecuteCommand<A, R>() {
                            @Override
                            public DispatchRequest execute(A action,
                                    AsyncCallback<R> resultCallback) {
                                if (dispatchRequest.isPending()) {
                                    return doExecute(securityCookie, action, resultCallback);
                                } else {
                                    return null;
                                }
                            }
                        }));
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    dispatchRequest.cancel();
                    callback.onFailure(caught);
                }
            });
            return dispatchRequest;

        } else {
            return doExecute(securityCookie, action, callback);
        }
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(final A action, final R result,
            final AsyncCallback<Void> callback) {
        final String securityCookie = securityCookieAccessor.getCookieContent();

        final IndirectProvider<ClientActionHandler<?, ?>> clientActionHandlerProvider = clientActionHandlerRegistry
                .find(action.getClass());

        if (clientActionHandlerProvider != null) {
            final DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
            clientActionHandlerProvider.get(new AsyncCallback<ClientActionHandler<?, ?>>() {
                @Override
                public void onSuccess(ClientActionHandler<?, ?> clientActionHandler) {

                    if (clientActionHandler.getActionType() != action.getClass()) {
                        dispatchRequest.cancel();
                        callback.onFailure(new ClientActionHandlerMismatchException(
                                (Class<? extends Action<?>>) action.getClass(), clientActionHandler.getActionType()));
                        return;
                    }

                    if (dispatchRequest.isPending()) {
                        dispatchRequest.setDelegate(((ClientActionHandler<A, R>) clientActionHandler).undo(
                                action, result, callback, new UndoCommand<A, R>() {

                            @Override
                            public DispatchRequest undo(A action, R result,
                                    AsyncCallback<Void> callback) {
                                if (dispatchRequest.isPending()) {
                                    return doUndo(securityCookie, action, result, callback);
                                } else {
                                    return null;
                                }
                            }
                        }));
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    dispatchRequest.cancel();
                    callback.onFailure(caught);
                }
            });
            return dispatchRequest;

        } else {
            return doUndo(securityCookie, action, result, callback);
        }
    }

    protected abstract <A extends Action<R>, R extends Result> DispatchRequest doExecute(String securityCookie,
            final A action, final AsyncCallback<R> callback);

    protected abstract <A extends Action<R>, R extends Result> DispatchRequest doUndo(String securityCookie,
            final A action, R result, final AsyncCallback<Void> callback);

    protected ClientActionHandlerRegistry getClientActionHandlerRegistry() {
        return clientActionHandlerRegistry;
    }

    protected ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    protected SecurityCookieAccessor getSecurityCookieAccessor() {
        return securityCookieAccessor;
    }

    protected <A extends Action<R>, R extends Result> void onExecuteFailure(A action, Throwable caught,
            final AsyncCallback<R> callback) {
        if (getExceptionHandler() != null
                && getExceptionHandler().onFailure(caught) == ExceptionHandler.Status.STOP) {
            return;
        }

        callback.onFailure(caught);
    }

    protected <A extends Action<R>, R extends Result> void onExecuteSuccess(A action, R result,
            final AsyncCallback<R> callback) {
        callback.onSuccess(result);
    }

    protected <A extends Action<R>, R extends Result> void onUndoFailure(A action, Throwable caught,
            final AsyncCallback<Void> callback) {
        if (getExceptionHandler() != null
                && getExceptionHandler().onFailure(caught) == ExceptionHandler.Status.STOP) {
            return;
        }
        callback.onFailure(caught);
    }

    protected <A extends Action<R>, R extends Result> void onUndoSuccess(A action, Void voidResult,
            final AsyncCallback<Void> callback) {
        callback.onSuccess(voidResult);
    }

    protected <A extends Action<R>, R extends Result> void prepareExecute(final A action) {
    }

    protected <A extends Action<R>, R extends Result> void prepareUndo(final A action) {
    }
}
