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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.DispatchService;
import com.gwtplatform.dispatch.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * This class is the default implementation of {@link DispatchAsync}, which is
 * essentially the client-side access to the
 * {@link com.gwtplatform.dispatch.server.Dispatch} class on the server-side.
 */
public class DefaultDispatchAsync extends AbstractDispatchAsync {
    private static final DispatchServiceAsync realService = GWT.create(DispatchService.class);
    private final String baseUrl;

    public DefaultDispatchAsync(ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            ClientActionHandlerRegistry registry) {
        super(exceptionHandler, securityCookieAccessor, registry);

        String entryPointUrl = ((ServiceDefTarget) realService).getServiceEntryPoint();
        if (entryPointUrl == null) {
            this.baseUrl = "";
        } else {
            this.baseUrl = entryPointUrl;
        }
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doExecute(
            String securityCookie, final A action, final AsyncCallback<R> callback) {
        return new GwtHttpDispatchRequest(realService.execute(securityCookie, action, new AsyncCallback<Result>() {
            public void onFailure(Throwable caught) {
                DefaultDispatchAsync.this.onExecuteFailure(action, caught, callback);
            }

            @SuppressWarnings("unchecked")
            public void onSuccess(Result result) {
                // Note: This cast is a dodgy hack to get around a GWT
                // 1.6 async compiler issue
                DefaultDispatchAsync.this.onExecuteSuccess(action, (R) result, callback);
            }
        }));
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doUndo(
            String securityCookie, final A action, final R result,
            final AsyncCallback<Void> callback) {

        return new GwtHttpDispatchRequest(realService.undo(securityCookie, action, result, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                DefaultDispatchAsync.this.onUndoFailure(action, caught, callback);
            }

            public void onSuccess(Void voidResult) {
                DefaultDispatchAsync.this.onUndoSuccess(action, voidResult, callback);
            }
        }));
    }

    @Override
    protected <A extends Action<R>, R extends Result> void prepareExecute(A action) {
        prepareService((ServiceDefTarget) realService, baseUrl, action.getServiceName());
    }

    protected void prepareService(ServiceDefTarget service, final String moduleUrl, String relativeServiceUrl) {
        service.setServiceEntryPoint(moduleUrl + relativeServiceUrl);
    }

    @Override
    protected <A extends Action<R>, R extends Result> void prepareUndo(A action) {
        prepareService((ServiceDefTarget) realService, baseUrl, action.getServiceName());
    }
}
