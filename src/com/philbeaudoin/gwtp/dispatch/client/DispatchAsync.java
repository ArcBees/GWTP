package com.philbeaudoin.gwtp.dispatch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This is an asynchronous equivalent of the {@link Dispatch} interface on the
 * server side. The reason it exists is because GWT currently can't correctly
 * handle having generic method templates in method signatures (eg.
 * <code>&lt;A&gt; A
 * create( Class<A> type )</code>)
 * 
 * @author David Peterson
 */
public interface DispatchAsync {
    <A extends Action<R>, R extends Result> void execute( A action, AsyncCallback<R> callback );
}
