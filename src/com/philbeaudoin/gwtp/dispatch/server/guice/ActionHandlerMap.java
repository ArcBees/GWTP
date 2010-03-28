package com.philbeaudoin.gwtp.dispatch.server.guice;

import com.philbeaudoin.gwtp.dispatch.server.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public interface ActionHandlerMap<A extends Action<R>, R extends Result> {
    public Class<A> getActionClass();
    
    public Class<? extends ActionHandler<A, R>> getActionHandlerClass();
}
