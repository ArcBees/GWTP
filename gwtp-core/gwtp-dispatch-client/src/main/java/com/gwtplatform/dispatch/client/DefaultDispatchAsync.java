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

package com.gwtplatform.dispatch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerMismatchException;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.client.actionhandler.UndoCommand;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.DispatchService;
import com.gwtplatform.dispatch.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * This class is the default implementation of {@link DispatchAsync}, which is
 * essentially the client-side access to the
 * {@link com.gwtplatform.dispatch.server.Dispatch} class on the server-side.
 *
 * @author David Peterson
 * @author Christian Goudreau
 * @author Brendan Doherty
 */
public class DefaultDispatchAsync implements DispatchAsync {
  private static final DispatchServiceAsync realService = GWT.create(DispatchService.class);
  private final String baseUrl;
  private final ExceptionHandler exceptionHandler;
  private final SecurityCookieAccessor securityCookieAccessor;
  private final ClientActionHandlerRegistry registry;

  public DefaultDispatchAsync(ExceptionHandler exceptionHandler,
      SecurityCookieAccessor securityCookieAccessor,
      ClientActionHandlerRegistry registry) {
    this.exceptionHandler = exceptionHandler;
    this.securityCookieAccessor = securityCookieAccessor;
    this.registry = registry;
    String entryPointUrl = ((ServiceDefTarget) realService).getServiceEntryPoint();
    if (entryPointUrl == null) {
      this.baseUrl = "";
    } else {
      this.baseUrl = entryPointUrl;
    }
  }

  @SuppressWarnings("unchecked")
  public <A extends Action<R>, R extends Result> DispatchRequest execute(
      final A action, final AsyncCallback<R> callback) {
    ((ServiceDefTarget) realService).setServiceEntryPoint(baseUrl
        + action.getServiceName());

    final String securityCookie = securityCookieAccessor.getCookieContent();

    final IndirectProvider<ClientActionHandler<?, ?>> clientActionHandlerProvider = registry.find(action.getClass());

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
                      return serviceExecute(securityCookie, action,
                          resultCallback);
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

      return serviceExecute(securityCookie, action, callback);
    }
  }

  private <A extends Action<R>, R extends Result> DispatchRequest serviceExecute(
      String securityCookie, final A action, final AsyncCallback<R> callback) {
    return new GwtHttpDispatchRequest(realService.execute(securityCookie,
        action, new AsyncCallback<Result>() {
          public void onFailure(Throwable caught) {
            DefaultDispatchAsync.this.onExecuteFailure(action, caught, callback);
          }

          @SuppressWarnings("unchecked")
          public void onSuccess(Result result) {

            // Note: This cast is a dodgy hack to get around a GWT
            // 1.6 async
            // compiler issue
            DefaultDispatchAsync.this.onExecuteSuccess(action, (R) result,
                callback);
          }
        }));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends Action<R>, R extends Result> DispatchRequest undo(
      final A action, final R result, final AsyncCallback<Void> callback) {
    ((ServiceDefTarget) realService).setServiceEntryPoint(baseUrl
        + action.getServiceName());

    final String securityCookie = securityCookieAccessor.getCookieContent();

    final IndirectProvider<ClientActionHandler<?, ?>> clientActionHandlerProvider = registry.find(action.getClass());

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
                      return serviceUndo(securityCookie, action, result,
                          callback);
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

      return serviceUndo(securityCookie, action, result, callback);
    }
  }

  private <A extends Action<R>, R extends Result> DispatchRequest serviceUndo(
      String securityCookie, final A action, final R result,
      final AsyncCallback<Void> callback) {

    return new GwtHttpDispatchRequest(realService.undo(securityCookie, action,
        result, new AsyncCallback<Void>() {
          public void onFailure(Throwable caught) {
            DefaultDispatchAsync.this.onUndoFailure(action, caught, callback);
          }

          public void onSuccess(Void voidResult) {
            DefaultDispatchAsync.this.onUndoSuccess(action, voidResult,
                callback);
          }
        }));
  }

  protected <A extends Action<R>, R extends Result> void onExecuteFailure(
      A action, Throwable caught, final AsyncCallback<R> callback) {
    if (exceptionHandler != null
        && exceptionHandler.onFailure(caught) == ExceptionHandler.Status.STOP) {
      return;
    }

    callback.onFailure(caught);
  }

  protected <A extends Action<R>, R extends Result> void onExecuteSuccess(
      A action, R result, final AsyncCallback<R> callback) {
    callback.onSuccess(result);
  }

  protected <A extends Action<R>, R extends Result> void onUndoFailure(
      A action, Throwable caught, final AsyncCallback<Void> callback) {
    if (exceptionHandler != null
        && exceptionHandler.onFailure(caught) == ExceptionHandler.Status.STOP) {
      return;
    }

    callback.onFailure(caught);
  }

  protected <A extends Action<R>, R extends Result> void onUndoSuccess(
      A action, Void voidResult, final AsyncCallback<Void> callback) {
    callback.onSuccess(voidResult);
  }
}
