package com.philbeaudoin.gwtp.dispatch.server.standard;

/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
