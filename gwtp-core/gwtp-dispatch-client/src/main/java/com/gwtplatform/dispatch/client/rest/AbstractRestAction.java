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

import java.util.HashMap;
import java.util.Map;

import com.gwtplatform.dispatch.shared.Result;

/**
 * TODO: Documentation
 */
public abstract class AbstractRestAction<R extends Result> implements RestAction<R> {
    private HttpMethod httpMethod;
    private String rawServicePath;

    private HashMap<String, Object> pathParams = new HashMap<String, Object>();
    private HashMap<String, Object> queryParams = new HashMap<String, Object>();
    private HashMap<String, Object> headerParams = new HashMap<String, Object>();
    private HashMap<String, Object> formParams = new HashMap<String, Object>();

    protected AbstractRestAction(HttpMethod httpMethod, String rawServicePath) {
        this.httpMethod = httpMethod;
        this.rawServicePath = rawServicePath;
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
    public Map<String, Object> getPathParams() {
        return pathParams;
    }

    @Override
    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    @Override
    public Map<String, Object> getFormParams() {
        return formParams;
    }

    @Override
    public Map<String, Object> getHeaderParams() {
        return headerParams;
    }

    protected void putPathParam(String key, Object value) {
        pathParams.put(key, value);
    }

    protected void putQueryParam(String key, Object value) {
        queryParams.put(key, value);
    }

    protected void putFormParam(String key, Object value) {
        formParams.put(key, value);
    }

    protected void putHeaderParam(String key, Object value) {
        headerParams.put(key, value);
    }
}
