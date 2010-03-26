package com.philbeaudoin.gwtp.dispatch.client.standard;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardDispatchServiceAsync {

    /**
     * Executes the specified action.
     * 
     * @param action The action to execute.
     * @param callback The callback to execute once the action completes.
     * 
     * @see com.philbeaudoin.gwtp.dispatch.server.Dispatch
     */
    void execute( Action<?> action, AsyncCallback<Result> callback );
}
