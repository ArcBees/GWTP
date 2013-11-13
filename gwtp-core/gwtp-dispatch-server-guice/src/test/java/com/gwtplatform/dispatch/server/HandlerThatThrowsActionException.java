package com.gwtplatform.dispatch.server;

import javax.inject.Inject;

import com.gwtplatform.dispatch.server.actionhandler.AbstractActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.NoResult;

public class HandlerThatThrowsActionException extends AbstractActionHandler<SomeAction, NoResult> {
    @Inject
    HandlerThatThrowsActionException() {
        super(SomeAction.class);
    }

    @Override
    public NoResult execute(SomeAction action, ExecutionContext context) throws ActionException {
        throw new ActionExceptionThrownByHandler();
    }

    @Override
    public void undo(SomeAction action, NoResult result, ExecutionContext context) throws ActionException {
    }
}
