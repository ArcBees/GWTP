package com.arcbees.carsample.server.dispatch;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;

public abstract class AbstractActionHandler<A extends Action<R>, R extends Result>
        implements ActionHandler<A, R> {
    private final Class<A> actionType;

    public AbstractActionHandler(Class<A> actionType) {
        this.actionType = actionType;
    }

    public Class<A> getActionType() {
        return actionType;
    }

    @Override
    public void undo(A action, R result, ExecutionContext context)
            throws ActionException {
    }
}
