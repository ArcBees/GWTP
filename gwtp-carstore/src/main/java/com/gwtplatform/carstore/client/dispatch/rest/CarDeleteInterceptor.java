/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.carstore.client.dispatch.rest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.dispatch.client.interceptor.ExecuteCommand;
import com.gwtplatform.dispatch.rest.client.interceptor.AbstractRestInterceptor;
import com.gwtplatform.dispatch.rest.client.interceptor.InterceptorContext;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

public class CarDeleteInterceptor extends AbstractRestInterceptor {
    CarDeleteInterceptor() {
        super(new InterceptorContext.Builder()
            .path(ResourcesPath.CARS)
            .httpMethod(HttpMethod.DELETE)
            .transcendent(true)
            .build());
    }

    @Override
    public DispatchRequest execute(RestAction action, AsyncCallback<Object> resultCallback,
            ExecuteCommand<RestAction, Object> executeCommand) {
        return executeCommand.execute(action, resultCallback);
    }
}
