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

package com.gwtplatform.dispatch.rpc.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import jakarta.inject.Inject;

/**
 * This class is the default implementation of {@link com.gwtplatform.dispatch.rpc.shared.DispatchAsync}, which is
 * essentially the client-side access to the {@code com.gwtplatform.dispatch.rpc.server.Dispatch} class on the
 * server-side.
 */
public class RpcDispatchAsync implements DispatchAsync {
    private final DispatchServiceAsync realService;
    private final RpcDispatchCallFactory rpcDispatchCallFactory;
    private final String baseUrl;

    @Inject
    RpcDispatchAsync(RpcDispatchCallFactory rpcDispatchCallFactory,
                     DispatchServiceAsync dispatchService) {
        this.realService = dispatchService;
        this.rpcDispatchCallFactory = rpcDispatchCallFactory;

        String entryPointUrl = ((ServiceDefTarget) realService).getServiceEntryPoint();
        if (entryPointUrl == null) {
            this.baseUrl = "";
        } else {
            this.baseUrl = entryPointUrl;
        }
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        prepareExecute(action);

        RpcDispatchExecuteCall<A, R> call = rpcDispatchCallFactory.create(action, callback);
        return call.execute();
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result,
                                                                        AsyncCallback<Void> callback) {
        prepareUndo(action);

        RpcDispatchUndoCall<A, R> call = rpcDispatchCallFactory.create(action, result, callback);
        return call.execute();
    }

    protected <A extends Action<R>, R extends Result> void prepareExecute(A action) {
        prepareService((ServiceDefTarget) realService, baseUrl, action.getServiceName());
    }

    protected <A extends Action<R>, R extends Result> void prepareUndo(A action) {
        prepareService((ServiceDefTarget) realService, baseUrl, action.getServiceName());
    }

    protected void prepareService(ServiceDefTarget service, String moduleUrl, String relativeServiceUrl) {
        service.setServiceEntryPoint(moduleUrl + relativeServiceUrl);
    }
}
