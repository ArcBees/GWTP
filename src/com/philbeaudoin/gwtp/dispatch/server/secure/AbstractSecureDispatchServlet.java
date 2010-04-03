package com.philbeaudoin.gwtp.dispatch.server.secure;

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



import com.philbeaudoin.gwtp.dispatch.client.secure.SecureDispatchService;
import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;
import com.philbeaudoin.gwtp.dispatch.shared.secure.InvalidSessionException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public abstract class AbstractSecureDispatchServlet extends RemoteServiceServlet implements SecureDispatchService {

  private static final long serialVersionUID = -1995842556570759707L;

  public Result execute( String sessionId, Action<?> action ) throws ActionException, ServiceException {
    try {
      if ( getSessionValidator().isValid( sessionId, getThreadLocalRequest() ) ) {      
        return getDispatch().execute( action );
      } else {
        throw new InvalidSessionException();
      }
    } catch ( ActionException e ) {
      log( "Action exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
      throw e;
    } catch ( ServiceException e ) {
      log( "Service exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
      throw e;
    } catch ( RuntimeException e ) {
      log( "Unexpected exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
      throw new ServiceException( e );
    }  }

  protected abstract SecureSessionValidator getSessionValidator();

  protected abstract Dispatch getDispatch();
}