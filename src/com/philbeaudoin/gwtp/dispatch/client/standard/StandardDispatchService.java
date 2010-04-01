package com.philbeaudoin.gwtp.dispatch.client.standard;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.philbeaudoin.gwtp.dispatch.client.DispatchAsync;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

/**
 * There currently seem to be GWT compilation problems with services that use
 * generic templates in method parameters. As such, they are stripped here, but
 * added back into the {@link com.philbeaudoin.gwtp.dispatch.server.Dispatch} 
 * and {@link DispatchAsync}, which are the interfaces that should be accessed 
 * by regular code.
 * <p/>
 * Once GWT can compile templatized methods correctly, this should be merged
 * into a single pair of interfaces.
 *
 * @author David Peterson
 */
@RemoteServiceRelativePath("dispatch")
public interface StandardDispatchService extends RemoteService {
    Result execute( Action<?> action ) throws ActionException, ServiceException;
}
