package com.gwtplatform.dispatch.rpc.client.interceptor;

import org.junit.Test;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.interceptor.ExecuteCommand;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.TypedAction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractRpcInterceptorTest {
    private static class TypedAction1 implements TypedAction<Result> {
    }

    private static class TypedAction2 implements TypedAction<Result> {
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
                                           ExecuteCommand<A, R> executeCommand) {
                return executeCommand.execute(action, resultCallback);
            }
        };
    }
}
