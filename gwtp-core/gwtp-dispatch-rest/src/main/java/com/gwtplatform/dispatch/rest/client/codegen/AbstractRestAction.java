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

package com.gwtplatform.dispatch.rest.client.codegen;

import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Provides a basic implementation of {@link RestAction} to inheritors. This is used by GWTP's code-generators to create
 * the actions defined by the resources.
 *
 * @param <R> the result type
 */
public abstract class AbstractRestAction<R> implements RestAction<R> {
    private final HttpMethod httpMethod;
    private final String path;
    private final List<HttpParameter> parameters;

    private Object bodyParam;

    /**
     * Creates a new instance of the action without parsing the regular expressions from the rawServicePath.
     *
     * @param httpMethod of the action
     * @param path the path after parsing the regex definitions
     */
    protected AbstractRestAction(
            HttpMethod httpMethod,
            String path) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.parameters = new ArrayList<>();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<HttpParameter> getParameters(Type type) {
        List<HttpParameter> filteredParams = new ArrayList<>();
        for (HttpParameter parameter : parameters) {
            if (parameter.getType() == type && parameter.getObject() != null) {
                filteredParams.add(parameter);
            }
        }

        return filteredParams;
    }

    @Override
    public Object getBodyParam() {
        return bodyParam;
    }

    @Override
    public Boolean hasFormParams() {
        return !getParameters(Type.FORM).isEmpty();
    }

    @Override
    public Boolean hasBodyParam() {
        return bodyParam != null;
    }

    protected void addParam(HttpParameter parameter) {
        parameters.add(parameter);
    }

    protected void setBodyParam(Object value) {
        bodyParam = value;
    }
}
