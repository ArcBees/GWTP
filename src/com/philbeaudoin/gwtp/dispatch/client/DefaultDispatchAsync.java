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

package com.philbeaudoin.gwtp.dispatch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This class is the default implementation of {@link DispatchAsync}, which is
 * essentially the client-side access to the
 * {@link com.philbeaudoin.gwtp.dispatch.server.Dispatch} class on the
 * server-side.
 * 
 * @author David Peterson
 * @author Christian Goudreau
 */
public class DefaultDispatchAsync implements DispatchAsync {
  private static final DispatchServiceAsync realService = GWT.create(DispatchService.class);
  private final ExceptionHandler exceptionHandler;
  private final SecurityCookieAccessor securityCookieAccessor;
  private final String baseUrl; 

  public DefaultDispatchAsync(ExceptionHandler exceptionHandler, SecurityCookieAccessor securityCookieAccessor) {
    this.exceptionHandler = exceptionHandler;
    this.securityCookieAccessor = securityCookieAccessor;
    String entryPointUrl = ((ServiceDefTarget) realService).getServiceEntryPoint();
    if( entryPointUrl == null )
      this.baseUrl = "";
    else
      this.baseUrl = entryPointUrl;
  }

  public <A extends Action<R>, R extends Result> void execute(final A action, final AsyncCallback<R> callback) {
    ((ServiceDefTarget) realService).setServiceEntryPoint(baseUrl + action.getServiceName());

    String securityCookie = securityCookieAccessor.getCookieContent();

    realService.execute(securityCookie, action, new AsyncCallback<Result>() {
      public void onFailure(Throwable caught) {
        DefaultDispatchAsync.this.onExecuteFailure(action, caught, callback);
      }

      @SuppressWarnings("unchecked")
      public void onSuccess(Result result) {
        // Note: This cast is a dodgy hack to get around a GWT 1.6 async
        // compiler issue
        DefaultDispatchAsync.this.onExecuteSuccess(action, (R) result, callback);
      }
    });
  }

  @Override
  public <A extends Action<R>, R extends Result> void undo(final A action, final R result,
      final AsyncCallback<Void> callback) {
    ((ServiceDefTarget) realService).setServiceEntryPoint(baseUrl + action.getServiceName());

    String securityCookie = securityCookieAccessor.getCookieContent();

    realService.undo(securityCookie, action, result, new AsyncCallback<Void>() {
      public void onFailure(Throwable caught) {
        DefaultDispatchAsync.this.onUndoFailure(action, caught, callback);
      }

      public void onSuccess(Void voidResult) {
        DefaultDispatchAsync.this.onUndoSuccess(action, voidResult, callback);
      }
    });
  }

  protected <A extends Action<R>, R extends Result> void onExecuteSuccess(A action, R result, final AsyncCallback<R> callback) {
    callback.onSuccess(result);
  }

  protected <A extends Action<R>, R extends Result> void onExecuteFailure(A action, Throwable caught, final AsyncCallback<R> callback) {
    if (exceptionHandler != null && exceptionHandler.onFailure(caught) == ExceptionHandler.Status.STOP) {
      return;
    }

    callback.onFailure(caught);
  }

  protected <A extends Action<R>, R extends Result> void onUndoSuccess(A action, Void voidResult, final AsyncCallback<Void> callback) {
    callback.onSuccess(voidResult);
  }

  protected <A extends Action<R>, R extends Result> void onUndoFailure(A action, Throwable caught, final AsyncCallback<Void> callback) {
    if (exceptionHandler != null && exceptionHandler.onFailure(caught) == ExceptionHandler.Status.STOP) {
      return;
    }

    callback.onFailure(caught);
  }
}
