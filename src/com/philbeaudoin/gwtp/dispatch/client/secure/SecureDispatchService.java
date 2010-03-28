package com.philbeaudoin.gwtp.dispatch.client.secure;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("secureDispatch")
public interface SecureDispatchService extends RemoteService {
    Result execute( String sessionId, Action<?> action ) throws ActionException, ServiceException;
}
