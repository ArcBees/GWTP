package com.philbeaudoin.gwtp.dispatch.client.secure;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public interface SecureDispatchServiceAsync {
    void execute( String sessionId, Action<?> action, AsyncCallback<Result> callback );
}
