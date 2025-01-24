/*
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

package com.gwtplatform.tester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.client.actionhandler.UndoCommand;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.dispatch.rpc.shared.DispatchService;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * This class is an implementation of {@link DispatchAsync} for use with test cases that configure guice using a {@link
 * MockHandlerModule}.
 */
public class TestDispatchAsync implements DispatchAsync {
    private final DispatchService service;
    private final Map<Class<?>, ClientActionHandler<?, ?>> clientActionHandlers;

    class TestingDispatchRequest implements DispatchRequest {

        TestingDispatchRequest() {
        }

        @Override
        public void cancel() {
        }

        @Override
        public boolean isPending() {
            return false;
        }
    }

    @Inject
    public TestDispatchAsync(TestDispatchService service, Injector injector) {
        this.service = service;

        clientActionHandlers = new HashMap<Class<?>, ClientActionHandler<?, ?>>();

        List<Binding<MockClientActionHandlerMap>> bindings = injector.findBindingsByType(
                TypeLiteral.get(MockClientActionHandlerMap.class));
        for (Binding<MockClientActionHandlerMap> binding : bindings) {
            MockClientActionHandlerMap mapping = binding.getProvider().get();
            clientActionHandlers.put(mapping.getActionClass(),
                    mapping.getClientActionHandler());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(
            A action, AsyncCallback<R> callback) {
        assert callback != null;
        ClientActionHandler<?, ?> clientActionHandler = clientActionHandlers.get(action.getClass());
        if (clientActionHandler != null) {
            DelegatingDispatchRequest request = new DelegatingDispatchRequest();
            ((ClientActionHandler<A, R>) clientActionHandler).execute(action,
                    callback, new ExecuteCommand<A, R>() {
                        @Override
                        public DispatchRequest execute(A action,
                                AsyncCallback<R> resultCallback) {
                            return serviceExecute(action, resultCallback);
                        }
                    });
            return request;
        } else {
            serviceExecute(action, callback);
            return new TestingDispatchRequest();
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Action<R>, R extends Result> DispatchRequest serviceExecute(
            A action, AsyncCallback<R> callback) {

        boolean fail = false;
        R result = null;
        try {
            result = (R) service.execute("", action);
        } catch (Throwable caught) {
            fail = true;
            callback.onFailure(caught);
        }
        if (!fail) {
            callback.onSuccess(result);
        }
        return new CompletedDispatchRequest();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action,
            R result, AsyncCallback<Void> callback) {

        ClientActionHandler<?, ?> clientActionHandler = clientActionHandlers.get(action.getClass());
        if (clientActionHandler != null) {
            DelegatingDispatchRequest request = new DelegatingDispatchRequest();
            ((ClientActionHandler<A, R>) clientActionHandler).undo(action, result,
                    callback, new UndoCommand<A, R>() {
                        @Override
                        public DispatchRequest undo(A action, R result, AsyncCallback<Void> callback) {
                            return serviceUndo(action, result, callback);
                        }
                    });
            return request;
        } else {
            serviceUndo(action, result, callback);
            return new TestingDispatchRequest();
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Action<R>, R extends Result> DispatchRequest serviceUndo(A action,
            R result, AsyncCallback<Void> callback) {

        boolean fail = false;
        try {
            service.undo("", (Action<Result>) action, result);
        } catch (Throwable caught) {
            fail = true;
            callback.onFailure(caught);
        }
        if (!fail) {
            callback.onSuccess(null);
        }
        return new CompletedDispatchRequest();
    }
}
