package com.philbeaudoin.gwtp.dispatch.server.standard;

import com.philbeaudoin.gwtp.dispatch.client.standard.StandardDispatchService;
import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

public abstract class AbstractStandardDispatchServlet extends RemoteServiceServlet implements StandardDispatchService {

  /**
   * 
   */
  private static final long serialVersionUID = -8306745239523629935L;

  public Result execute( Action<?> action ) throws ActionException, ServiceException {
    try {
      return getDispatch().execute( action );
    } catch ( ActionException e ) {
      log( "Action exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
      throw e;
    } catch ( ServiceException e ) {
      log( "Service exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
      throw e;
    } catch ( RuntimeException e ) {
      log( "Unexpected exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
      throw new ServiceException( e );
    }
  }

  /**
   * 
   * @return The Dispatch instance.
   */
  protected abstract Dispatch getDispatch();

}
