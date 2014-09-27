/**
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

import com.gwtplatform.dispatch.rest.shared.HttpMethod;

/**
 * This exception is thrown when multiple {@link com.gwtplatform.dispatch.rest.client.interceptor.InterceptorContext}
 * are attempting to be registered. There can only be one InterceptorContext registered per REST interceptor.
 */
public class DuplicateInterceptorContextException extends RuntimeException {
    private String path;
    private HttpMethod httpMethod;
    private int queryParams;

    private DuplicateInterceptorContextException() { }

    public DuplicateInterceptorContextException(String path, HttpMethod httpMethod, int queryParams) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.queryParams = queryParams;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public int getQueryParams() {
        return queryParams;
    }

    @Override
    public String toString() {
        return getClass().getName() +
               " [path=" + this.path + "," +
               " httpMethod" + httpMethod + "," +
               " queryParams" + queryParams + "]";
    }

}
