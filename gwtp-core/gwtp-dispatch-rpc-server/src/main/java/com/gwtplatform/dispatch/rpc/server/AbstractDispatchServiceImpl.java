/*
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rpc.server;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;
import com.gwtplatform.dispatch.rpc.server.logger.DispatchServiceLogHandler;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchService;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.rpc.shared.ServiceException;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This is the server-side implementation of the {@link DispatchService}, for which the client-side async service is
 * {@link com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync}.
 * <p>
 * This class is closely related to {@link AbstractDispatchImpl}, in theory the latter wouldn't be needed, but we use it
 * to workaround a GWT limitation described in {@link com.gwtplatform.dispatch.rpc.shared.DispatchAsync}.
 *
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchAsync
 * @see Dispatch
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchService
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync
 */
public abstract class AbstractDispatchServiceImpl extends RemoteServiceServlet implements DispatchService {

    /**
     * The type of the method that can throw an exception.
     */
    public enum DispatchType {
        Execute,
        Undo
    }
    private static final long serialVersionUID = -4753225025940949024L;
    private static final String noSecurityCookieMessage = "You have to define a security cookie in order to use " +
            "secured actions. See com.gwtplatform.dispatch.shared" +
            ".SecurityCookie for details.";
    private static final String xsrfAttackMessage = "Cookie provided by RPC doesn't match request cookie, " +
            "aborting action, possible XSRF attack. (Maybe you forgot to set " +
            "the security cookie?)";
    protected final Dispatch dispatch;
    protected final DispatchServiceLogHandler logger;
    protected final RequestProvider requestProvider;

    protected AbstractDispatchServiceImpl(DispatchServiceLogHandler logger,
                                          Dispatch dispatch,
                                          RequestProvider requestProvider) {
        this.logger = logger;
        this.dispatch = dispatch;
        this.requestProvider = requestProvider;
    }

    public String getSecurityCookieName() {
        return null;
    }

    /**
     * {@link ActionException} and {@link ServiceException} will have their stacktraces (and stacktraces of their
     * causes) removed for security purposes.
     *
     * @see DispatchService DispatchService for further API docs
     */
    @Override
    public Result execute(String cookieSentByRPC, Action<?> action) throws ActionException, ServiceException {

        if (action.isSecured() && !cookieMatch(cookieSentByRPC)) {
            String message = xsrfAttackMessage + " While executing action: " + action.getClass().getName();

            logger.severe(message);
            throw new ServiceException(message);
        }

        try {
            return dispatch.execute(action);
        } catch (ActionException e) {
            logger.log(e, action, DispatchType.Execute);

            removeStacktraces(e);

            throw e;
        } catch (ServiceException e) {
            logger.log(e, action, DispatchType.Execute);
            throw new ServiceException(e.getMessage());
        } catch (RuntimeException e) {
            logger.log(e, action, DispatchType.Execute);
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void undo(String cookieSentByRPC, Action<Result> action, Result result) throws ActionException,
            ServiceException {

        if (action.isSecured() && !cookieMatch(cookieSentByRPC)) {
            String message = xsrfAttackMessage + " While undoing action: " + action.getClass().getName();

            logger.severe(message);
            throw new ServiceException(message);
        }

        try {
            dispatch.undo(action, result);
        } catch (ActionException e) {
            logger.log(e, action, DispatchType.Undo);

            throw new ActionException(e.getMessage());
        } catch (ServiceException e) {
            logger.log(e, action, DispatchType.Undo);

            throw new ServiceException(e.getMessage());
        } catch (RuntimeException e) {
            logger.log(e, action, DispatchType.Undo);

            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Checks that the cookie in the RPC matches the one in the http request header.
     *
     * @param cookieSentByRPC The content of the security cookie sent by RPC.
     * @return {@code true} if the cookies match, {@code false} otherwise.
     * @throws ServiceException If you forgot to bind a {@link com.gwtplatform.dispatch.shared.SecurityCookie}.
     */
    private boolean cookieMatch(String cookieSentByRPC) throws ServiceException {

        // Make sure the specified cookie matches the
        HttpServletRequest request = requestProvider.getServletRequest();

        if (getSecurityCookieName() == null) {
            logger.info(noSecurityCookieMessage);
            return false;
        }

        if (cookieSentByRPC == null) {
            logger.info("No cookie sent by client in RPC. (Did you forget to bind the security cookie client-side? Or" +
                        " it could be an attack.)");
            return false;
        }

        // Try to match session tokens to prevent XSRF
        Cookie[] cookies = request.getCookies();
        String cookieInRequest = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(getSecurityCookieName())) {
                    cookieInRequest = cookie.getValue();
                    break;
                }
            }
        }

        if (cookieInRequest == null) {
            logger.info("Cookie \"" + getSecurityCookieName() + "\" not found in HttpServletRequest!");
            return false;
        }

        return cookieInRequest.equals(cookieSentByRPC);
    }

    /**
     * Recursively removes all stacktraces from a Throwable and its cause.
     */
    private void removeStacktraces(Throwable e) {
        if (e == null) {
            return;
        }

        e.setStackTrace(new StackTraceElement[]{});
        removeStacktraces(e.getCause());
    }
}
