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

package com.gwtplatform.dispatch.rest.client.actionhandler;

import com.gwtplatform.dispatch.rest.shared.HttpMethod;

/**
 * Index class used for the Rest Action Handler mappings.<br>
 * <pre>
 *     new RestHandlerIndex("/items", HttpMethod.GET, 2);
 * or:
 *     RestAction action = getAction();
 *     new RestHandlerIndex(action.getPath(), action.getHttpMethod(), action.getQueryParams().size());
 * </pre>
 */
public class RestHandlerIndex {

    private final String path;
    private final HttpMethod method;
    private final int queryCount;

    public RestHandlerIndex(String path, HttpMethod method, int queryCount) {
        this.path = path;
        this.method = method;
        this.queryCount = queryCount;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public int getQueryCount() {
        return queryCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        RestHandlerIndex that = (RestHandlerIndex) o;

        if (queryCount != that.queryCount) { return false; }
        if (method != that.method) { return false; }
        if (path != null ? !path.equals(that.path) : that.path != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + queryCount;
        return result;
    }
}
