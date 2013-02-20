/**
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

package com.gwtplatform.dispatch.client;

import javax.inject.Inject;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

public class PhoneGapDispatchAsync extends DefaultDispatchAsync {
    private final String remoteServerUrl;

    @Inject
    PhoneGapDispatchAsync(ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            ClientActionHandlerRegistry registry,
            @RemoteServerUrl String remoteServerUrl) {
        super(exceptionHandler, securityCookieAccessor, registry);

        this.remoteServerUrl = remoteServerUrl;
    }

    @Override
    protected void prepareService(ServiceDefTarget service, final String moduleUrl, String relativeServiceUrl) {
        service.setServiceEntryPoint(remoteServerUrl + relativeServiceUrl);
        service.setRpcRequestBuilder(new RpcRequestBuilder() {
            @Override
            protected void doFinish(RequestBuilder requestBuilder) {
                super.doFinish(requestBuilder);

                requestBuilder.setHeader(MODULE_BASE_HEADER, remoteServerUrl);
            }
        });
    }
}
