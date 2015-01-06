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

package com.gwtplatform.dispatch.rest.client;

import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.dispatch.rest.client.parameters.HttpParameterFactory;
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
    private final HttpParameterFactory httpParameterFactory;
    private final String defaultDateFormat;
    private final HttpMethod httpMethod;
    private final String rawServicePath;
    private final List<HttpParameter> parameters;

    private Object bodyParam;

    protected AbstractRestAction(
            HttpParameterFactory httpParameterFactory,
            String defaultDateFormat,
            HttpMethod httpMethod,
            String rawServicePath) {
        this.httpParameterFactory = httpParameterFactory;
        this.defaultDateFormat = defaultDateFormat;
        this.httpMethod = httpMethod;
        this.rawServicePath = rawServicePath;
        this.parameters = new ArrayList<HttpParameter>();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String getPath() {
        return rawServicePath;
    }

    @Override
    public List<HttpParameter> getParameters(Type type) {
        List<HttpParameter> filteredParams = new ArrayList<HttpParameter>();
        for (HttpParameter parameter : parameters) {
            if (parameter.getType() == type && parameter.getObject() != null) {
                filteredParams.add(parameter);
            }
        }

        return filteredParams;
    }

    @Override
    public boolean isSecured() {
        return false;
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

    protected void addParam(HttpParameter.Type type, String name, Object value) {
        addParam(type, name, value, defaultDateFormat);
    }

    protected void addParam(HttpParameter.Type type, String name, Object value, String dateFormat) {
        HttpParameter parameter = httpParameterFactory.create(type, name, value, dateFormat);
        parameters.add(parameter);
    }

    protected void setBodyParam(Object value) {
        bodyParam = value;
    }
}
