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

package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.rest.BodyParameter;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;
import com.gwtplatform.dispatch.shared.rest.ResponseParameter;
import com.gwtplatform.dispatch.shared.rest.RestAction;
import com.gwtplatform.dispatch.shared.rest.RestParameter;

/**
 * TODO: Documentation.
 */
public abstract class AbstractRestAction<R extends Result> implements RestAction<R> {
    private HttpMethod httpMethod;
    private String rawServicePath;

    private List<RestParameter> pathParams = new ArrayList<RestParameter>();
    private List<RestParameter> headerParams = new ArrayList<RestParameter>();
    private List<RestParameter> queryParams = new ArrayList<RestParameter>();
    private List<RestParameter> formParams = new ArrayList<RestParameter>();
    private BodyParameter bodyParam;
    private ResponseParameter responseParam;

    protected AbstractRestAction(HttpMethod httpMethod, String rawServicePath, String responseSerializerId) {
        this.httpMethod = httpMethod;
        this.rawServicePath = rawServicePath;

        responseParam = new ResponseParameter(responseSerializerId);
    }

    protected AbstractRestAction() {
    }

    @Override
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String getServiceName() {
        return rawServicePath;
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    @Override
    public List<RestParameter> getPathParams() {
        return pathParams;
    }

    @Override
    public List<RestParameter> getQueryParams() {
        return queryParams;
    }

    @Override
    public List<RestParameter> getFormParams() {
        return formParams;
    }

    @Override
    public List<RestParameter> getHeaderParams() {
        return headerParams;
    }

    @Override
    public BodyParameter getBodyParam() {
        return bodyParam;
    }

    @Override
    public ResponseParameter getResponseParam() {
        return responseParam;
    }

    @Override
    public Boolean hasFormParams() {
        return !formParams.isEmpty();
    }

    @Override
    public Boolean hasBodyParam() {
        return bodyParam != null;
    }

    protected void addPathParam(String name, Serializable value) {
        pathParams.add(new RestParameter(name, value));
    }

    protected void addQueryParam(String name, Serializable value) {
        queryParams.add(new RestParameter(name, value));
    }

    protected void addFormParam(String name, Serializable value) {
        formParams.add(new RestParameter(name, value));
    }

    protected void addHeaderParam(String name, Serializable value) {
        headerParams.add(new RestParameter(name, value));
    }

    protected void setBodyParam(Serializable value, String serializerId) {
        bodyParam = new BodyParameter(value, serializerId);
    }
}
