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

package com.gwtplatform.dispatch.rpc.server.guice;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.rpc.server.AbstractDispatchServiceImpl;
import com.gwtplatform.dispatch.rpc.server.Dispatch;
import com.gwtplatform.dispatch.rpc.server.RequestProvider;
import com.gwtplatform.dispatch.rpc.server.logger.DispatchServiceLogHandler;
import com.gwtplatform.dispatch.shared.SecurityCookie;

import jakarta.inject.Singleton;

/**
 * This is the server-side implementation of the {@link com.gwtplatform.dispatch.rpc.shared.DispatchService},
 * for which the client-side async service is {@link com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync}.
 * <p>
 * This class is closely related to {@link DispatchImpl}, in theory the latter wouldn't be needed, but we use it to
 * workaround a GWT limitation described in {@link com.gwtplatform.dispatch.rpc.shared.DispatchAsync}.
 *
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchAsync
 * @see com.gwtplatform.dispatch.rpc.server.Dispatch
 * @see DispatchImpl
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchService
 * @see com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync
 * @see DispatchServiceImpl
 */
@Singleton
public class DispatchServiceImpl extends AbstractDispatchServiceImpl {

    private static final long serialVersionUID = 136176741488585959L;

    @Inject(optional = true)
    @SecurityCookie
    protected String securityCookieName;

    @Inject
    public DispatchServiceImpl(final DispatchServiceLogHandler logger, final Dispatch dispatch, RequestProvider
            requestProvider) {
        super(logger, dispatch, requestProvider);
    }

    @Override
    public String getSecurityCookieName() {
        return securityCookieName;
    }
}
