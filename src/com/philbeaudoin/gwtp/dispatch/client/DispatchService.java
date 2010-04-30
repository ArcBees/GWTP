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

package com.philbeaudoin.gwtp.dispatch.client;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface DispatchService extends RemoteService {
    /**
     * This method is called server-side whenever a new action is dispatched.
     * 
     * @param cookieSentByRPC This is the content of the security cookie accessed on the client (in javascript),
     *                        its goal is to prevent XSRF attacks.
     *                        See {@link com.philbeaudoin.gwtp.dispatch.client.SecurityCookieAccessor} for more details.
     * @param action The {@link Action} to execute.
     * @return The {@link Result} of the action.
     * @throws ActionException Thrown if the action could not be executed, for example, because of lacks of detected 
     *                         while executing it.
     * @throws ServiceException Thrown if the action could not be executed because of a service error.
     */
    Result execute( String cookieSentByRPC, Action<?> action ) throws ActionException, ServiceException;
}
