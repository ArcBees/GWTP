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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.gwtplatform.dispatch.shared.HasSecured;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * An action represents a command sent to the com.gwtplatform.dispatch.rpc.server.Dispatch. It has a specific
 * result type which is returned if the action is successful. Your implementation should override
 * {@link #getServiceName} to return a default service url. If you use a
 * {@link com.gwtplatform.dispatch.shared.SecurityCookie} to prevent XSRF attacks and you want this action to be secured
 * against such attacks (i.e. it's not meant to be an anonymous action) then you should override {@link #isSecured()} to
 * return {@code true}.
 * <p>
 * You can usually inherit from {@link ActionImpl} or {@link UnsecuredActionImpl} instead.
 *
 * @param <R> The {@link Result} type.
 */
public interface Action<R extends Result> extends TypedAction<R>, HasSecured, IsSerializable {

    /**
     * The URL of the service used by default.
     */
    String DEFAULT_SERVICE_NAME = "dispatch/";

    /**
     * Access the name of the service, which will be used as the URL path to access the action.
     *
     * @return The service name.
     */
    String getServiceName();
}
