/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.client.interceptor;

import org.junit.Test;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.interceptor.ExecuteCommand;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractRpcInterceptorTest {
    private static class TypedAction1 implements Action<Result> {
        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public boolean isSecured() {
            return false;
        }
    }

    private static class TypedAction2 implements Action<Result> {
        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public boolean isSecured() {
            return false;
        }
    }

    @Test
    public void typedAction_typeMatch_canExecute() {
        AbstractRpcInterceptor<TypedAction1, Result> interceptor = createInterceptor(TypedAction1.class);

        boolean canExecute = interceptor.canExecute(new TypedAction1());

        assertTrue(canExecute);
    }

    @Test
    public void typedAction_typeDoesntMatch_cannotExecute() {
        AbstractRpcInterceptor<TypedAction1, Result> interceptor = createInterceptor(TypedAction1.class);

        boolean canExecute = interceptor.canExecute(new TypedAction2());

        assertFalse(canExecute);
    }

    private <A, R> AbstractRpcInterceptor<A, R> createInterceptor(Class<A> actionType) {
        return new AbstractRpcInterceptor<A, R>(actionType) {
            @Override
            public DispatchRequest undo(A action, R result, AsyncCallback<Void> callback,
                                        UndoCommand<A, R> undoCommand) {
                return undoCommand.undo(action, result, callback);
            }

            @Override
            public DispatchRequest execute(A action, AsyncCallback<R> resultCallback,
                                           ExecuteCommand<A, AsyncCallback<R>> executeCommand) {
                return executeCommand.execute(action, resultCallback);
            }
        };
    }
}
