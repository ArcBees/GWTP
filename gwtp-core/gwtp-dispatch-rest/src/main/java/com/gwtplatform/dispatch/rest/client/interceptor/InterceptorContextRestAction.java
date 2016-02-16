/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.interceptor;

import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * A RestAction stub used internally to compare interceptor contexts.
 */
// TODO: Consider not implementing RestAction because it brings unneeded methods
class InterceptorContextRestAction implements RestAction<Object> {
    private final HttpMethod httpMethod;
    private final String path;
    private final List<HttpParameter> parameters;

    InterceptorContextRestAction(HttpMethod httpMethod, String path, int queryCount) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.parameters = new ArrayList<HttpParameter>();

        // Add fake query params
        for (int i = 0; i < queryCount; i++) {
            parameters.add(new InterceptorContextHttpParameter("param" + i, "value" + i));
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public List<HttpParameter> getParameters(Type type) {
        return parameters;
    }

    @Override
    public Object getBodyParam() {
        return null;
    }

    @Override
    public Boolean hasFormParams() {
        return false;
    }

    @Override
    public Boolean hasBodyParam() {
        return false;
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    @Override
    public String getBodyClass() {
        return null;
    }

    @Override
    public String getResultClass() {
        return null;
    }

    @Override
    public List<ContentType> getClientProducedContentTypes() {
        return null;
    }

    @Override
    public List<ContentType> getClientConsumedContentTypes() {
        return null;
    }

    @Override
    public String getPathParameterRegex(String pathParameter) {
        return null;
    }

    @Override
    public String getRawServicePath() {
        return getPath();
    }
}
