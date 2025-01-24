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

package com.gwtplatform.dispatch.rpc.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * The base class of the synchronous dispatcher service. The server-side implementation is
 * com.gwtplatform.dispatch.rpc.server.guice.DispatchServiceImpl and the async client-side version is
 * {@link DispatchServiceAsync}.
 * <p>
 * This class is closely related to com.gwtplatform.dispatch.rpc.server.Dispatch, in theory the latter wouldn't
 * be needed, but we use it to workaround a GWT limitation described in
 * com.gwtplatform.dispatch.rpc.client.DispatchAsync.
 *
 * @see DispatchService
 * @see DispatchServiceAsync
 */
public interface DispatchService extends RemoteService {
    /**
     * This method is called server-side whenever a new action is dispatched.
     *
     * @param cookieSentByRPC This is the content of the security cookie accessed on the client (in javascript), its
     *                        goal is to prevent XSRF attacks. See
     *                        {@link com.gwtplatform.dispatch.shared.SecurityCookieAccessor} for more details.
     * @param action          The {@link Action} to execute.
     * @return The {@link Result} of the action.
     * @throws ActionException  Thrown if the action could not be executed for application-specific reasons. User
     *                          handlers should always throw {@link ActionException} or derived classes.
     * @throws ServiceException Thrown if the action could not be executed because of a service error.
     * @see DispatchServiceAsync#execute
     */
    Result execute(String cookieSentByRPC, Action<?> action) throws ActionException, ServiceException;

    /**
     * This method is called server-side whenever a previously executed action needs to be undone.
     *
     * @param cookieSentByRPC This is the content of the security cookie accessed on the client (in javascript), its
     *                        goal is to prevent XSRF attacks. See
     *                        {@link com.gwtplatform.dispatch.shared.SecurityCookieAccessor} for more details.
     * @param action          The {@link Action} to execute.
     * @param result          The {@link Result} of this action when it was executed.
     * @throws ActionException  Thrown if the action could not be undone for application-specific reasons. User handlers
     *                          should always throw {@link ActionException} or derived classes.
     * @throws ServiceException Thrown if the action could not be undone because of a service error.
     * @see DispatchServiceAsync#undo
     */
    void undo(String cookieSentByRPC, Action<Result> action, Result result) throws ActionException, ServiceException;
}
