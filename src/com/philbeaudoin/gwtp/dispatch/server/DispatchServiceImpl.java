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

import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.dispatch.client.DispatchService;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.SecurityCookie;
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

  private final static String xsrfAttackMessage =  "Cookie provided by RPC doesn't match request cookie, aborting action, possible XSRF attack. (Maybe you forgot to set the security cookie?)";
  private final static String noSecurityCookieMessage = "You have to define a security cookie in order to use secured actions. See com.philbeaudoin.gwtp.dispatch.shared.SecurityCookie for details.";
  
  protected final Logger logger;
  protected final Dispatch dispatch;
  protected Provider<HttpServletRequest> requestProvider;
  @Inject(optional=true)
  protected @SecurityCookie String securityCookieName = null;
  
  @Inject
  public DispatchServiceImpl(
      final Logger logger,
      final Dispatch dispatch,
      Provider<HttpServletRequest> requestProvider ) {
    this.logger = logger;
    this.dispatch = dispatch;
    this.requestProvider = requestProvider;
  }

  @Override
  public Result execute(String cookieSentByRPC, Action<?> action) throws ActionException, ServiceException {
    
    if( action.isSecured() && !cookieMatch(cookieSentByRPC) ) {
      logger.warning( xsrfAttackMessage + " During action: " + action.getClass().getName() );
      throw new ServiceException( xsrfAttackMessage + " During action: " + action.getClass().getName() );
    }

    try {
      return dispatch.execute(action);
    } catch (ActionException e) {
      logger.warning("Action exception while executing " + action.getClass().getName() + ": " + e.getMessage());
      throw e;
    } catch (ServiceException e) {
      logger.warning("Service exception while executing " + action.getClass().getName() + ": " + e.getMessage());
      throw e;
    } catch (RuntimeException e) {
      logger.warning("Unexpected exception while executing " + action.getClass().getName() + ": " + e.getMessage());
      throw new ServiceException(e);
    }
  }

  /**
   * Checks that the cookie in the RPC matches the one in the http request header.
   * 
   * @param cookieSentByRPC The content of the security cookie sent by RPC.
   * @return {@code true} if the cookies match, {@code false} otherwise.
   * @throws ServiceException If you forgot to bind a {@link SecurityCookie}.
   */
  private boolean cookieMatch(String cookieSentByRPC) throws ServiceException {
    
    // Make sure the specified cookie matches the
    HttpServletRequest request = requestProvider.get();

    if( securityCookieName == null ) {
      logger.severe( noSecurityCookieMessage );
      throw new ServiceException( noSecurityCookieMessage );    
    }

    if( cookieSentByRPC == null ) {
      logger.warning( "No cookie sent by client in RPC. (Did you forget to bind the security cookie client-side? Or it could be an attack.)" );
      return false;
    }
    
    // Try to match session tokens to prevent XSRF
    Cookie[] cookies = request.getCookies();
    String cookieInRequest = null;
    for ( Cookie cookie : cookies ) {
      if ( cookie.getName().equals( securityCookieName ) ) {
        cookieInRequest = cookie.getValue();
        break;
      }
    }
    
    if( cookieInRequest == null ) {
      logger.warning( "Cookie \"" + securityCookieName + "\" not found in HttpServletRequest!" );
      return false;
    }
    
    return cookieInRequest.equals( cookieSentByRPC );
  }
}