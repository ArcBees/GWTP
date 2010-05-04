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

package com.philbeaudoin.gwtp.dispatch.server;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.dispatch.client.DispatchService;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

/**
 * This is the server-side implementation of the {@link DispatchService}, for
 * which the client-side async service is {@link com.philbeaudoin.gwtp.dispatch.client.DispatchServiceAsync}.
 * <p />
 * This class is closely related to {@link DispatchImpl}, in theory the latter wouldn't
 * be needed, but we use it to workaround a GWT limitation described in {@link com.philbeaudoin.gwtp.dispatch.client.DispatchAsync}.
 * 
 * @see com.philbeaudoin.gwtp.dispatch.client.DispatchAsync
 * @see com.philbeaudoin.gwtp.dispatch.server.Dispatch
 * @see com.philbeaudoin.gwtp.dispatch.server.DispatchImpl
 * @see com.philbeaudoin.gwtp.dispatch.client.DispatchService
 * @see com.philbeaudoin.gwtp.dispatch.client.DispatchServiceAsync
 * @see com.philbeaudoin.gwtp.dispatch.server.DispatchServiceImpl
 * 
 * @author Christian Goudreau
 * @author David Peterson
 * 
 */
@Singleton
public class DispatchServiceImpl extends RemoteServiceServlet implements DispatchService {

  private static final long serialVersionUID = -4753225025940949024L;

  private final static String xsrfAttackMessage =  "Cookie provided by RPC doesn't match request cookie. Possible XSRF attack?";


  protected final Dispatch dispatch;
  protected Provider<HttpServletRequest> requestProvider;

  @Inject
  public DispatchServiceImpl(
      final Dispatch dispatch,
      Provider<HttpServletRequest> requestProvider ) {
    this.dispatch = dispatch;
    this.requestProvider = requestProvider;
  }

  public Result execute(String cookieSentByRPC, Action<?> action) throws ActionException, ServiceException {

    if( !cookieMatch(cookieSentByRPC) ) {
      log( xsrfAttackMessage + " During action: " + action.getClass().getName() );
      throw new ServiceException( "" );
    }

    try {
      return dispatch.execute(action);
    } catch (ActionException e) {
      log("Action exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e);
      throw e;
    } catch (ServiceException e) {
      log("Service exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e);
      throw e;
    } catch (RuntimeException e) {
      log("Unexpected exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e);
      throw new ServiceException(e);
    }
  }

  private boolean cookieMatch(String cookieSentByRPC) {
    // Make sure the specified cookie matches the
    HttpServletRequest request = requestProvider.get();

    // If on the local host we don't check for XSRF attacks
    // TODO Maybe we should, but we can't until we use our own cookie 
    String serverName = request.getServerName();
    if ( "localhost".equals( serverName ) || "127.0.0.1".equals( serverName ) )
      return true;

    // TODO We now completely bypass this test. We should solve the problem and test when solving issue 62.
    return true;
    
    // Try to match session tokens to prevent XSRF
//    Cookie[] cookies = request.getCookies();
//    String cookieInRequest = null;
//    for ( Cookie cookie : cookies ) {
//      if ( cookie.getName().equals( SecurityCookieAccessorImpl.COOKIE_NAME ) ) {
//        cookieInRequest = cookie.getValue();
//        break;
//      }
//    }
//    
//    if( cookieInRequest == null ) {
//      log( "Cookie \"" + SecurityCookieAccessorImpl.COOKIE_NAME + "\" not found in HttpServletRequest!" );
//      return false;
//    }
//    
//    return cookieInRequest.equals( cookieSentByRPC );
  }
}