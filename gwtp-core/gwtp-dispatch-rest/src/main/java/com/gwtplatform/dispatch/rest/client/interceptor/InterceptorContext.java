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

import java.util.List;

import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

/**
 * Index class used for the Rest Action Handler mappings.<br>
 * <pre>
 *     new InterceptorContext("/items", HttpMethod.GET, 2);
 * or:
 *     new InterceptorContext(getAction());
 * </pre>
 */
public class InterceptorContext {

    // Template definitions
    private RestAction<?> template;

    // Manual definitions
    private String path;
    private HttpMethod httpMethod;
    private int queryCount;

    // Check Properties
    private boolean transcendent;
    private boolean anyHttpMethod;
    private boolean anyQueryCount;

    private InterceptorContext() { }

    /**
     * Providing the action template that will define the context properties.
     * @param template RestAction with the context properties required.
     */
    public InterceptorContext(RestAction<?> template) {
        this(template, false);
    }

    /**
     * Providing the action template that will define the context properties.
     * @param template RestAction with the context properties required.
     * @param transcendent Use a transcendent strategy on the path, e.g. /path will be detected using /path/2.
     */
    public InterceptorContext(RestAction<?> template, boolean transcendent) {
        this(template, transcendent, false);
    }

    /**
     * Providing the action template that will define the context properties.
     * @param template RestAction with the context properties required.
     * @param transcendent Use a transcendent strategy on the path, e.g. /path will be detected using /path/2.
     * @param anyHttpMethod Allow any HTTP httpMethod when checking.
     */
    public InterceptorContext(RestAction<?> template, boolean transcendent, boolean anyHttpMethod) {
        this(template, transcendent, anyHttpMethod, false);
    }

    /**
     * Providing the action template that will define the context properties.
     * @param template RestAction with the context properties required.
     * @param transcendent Use a transcendent strategy on the path, e.g. /path will be detected using /path/2.
     * @param anyHttpMethod Allow any HTTP httpMethod when checking.
     * @param anyQueryCount Allow any query param count.
     */
    public InterceptorContext(RestAction<?> template, boolean transcendent, boolean anyHttpMethod,
                              boolean anyQueryCount) {
        this.template = template;
        this.transcendent = transcendent;
        this.anyHttpMethod = anyHttpMethod;
        this.anyQueryCount = anyQueryCount;
    }

    /**
     * Provide a specialized context definition.
     * @param path The actions resource path.
     * @param httpMethod The {@link HttpMethod} of the resource (use null for all httpMethod types).
     * @param queryCount The number of queries defined in the resource (-1 to ignore this property).
     * @param transcendent Use a transcendent strategy on the path, e.g. /path will be detected using /path/2.
     */
    public InterceptorContext(String path, HttpMethod httpMethod, int queryCount, boolean transcendent) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.queryCount = queryCount;
        this.transcendent = transcendent;

        if (httpMethod == null) {
            anyHttpMethod = true;
        }
        if (queryCount < 0) {
            anyQueryCount = true;
        }
    }

    public static InterceptorContext newContext(RestAction<?> action) {
        return new InterceptorContext(action);
    }

    protected boolean canIntercept(RestAction<?> action) {
        // Required check types
        if (useTemplate()) {
            path = template.getPath();
            httpMethod = template.getHttpMethod();
            queryCount = template.getQueryParams().size();
        }

        // Path Check
        if (isTranscendent() && !action.getPath().startsWith(path)) {
            return false;
        } else if (!action.getPath().equals(path)) {
            return false;
        } else {
            // Http Method Check
            if (!isAnyHttpMethod()) {
                if (!action.getHttpMethod().equals(httpMethod)) {
                    return false;
                }
            }

            // Query Parameters
            if (!anyQueryCount) {
                List<RestParameter> queryParams = action.getQueryParams();
                if (queryParams.size() != queryCount) {
                    return false;
                } else if (useTemplate()) {
                    // We can do some thorough checking with templates
                    if (!queryParams.equals(template.getQueryParams())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean useTemplate() {
        return template != null;
    }

    private boolean isAnyHttpMethod() {
        return anyHttpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public int getQueryCount() {
        return queryCount;
    }

    public RestAction getTemplate() {
        return template;
    }

    public boolean isTranscendent() {
        return transcendent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        InterceptorContext that = (InterceptorContext) o;
        RestAction action = null;
        if (that.useTemplate()) {
            action = that.getTemplate();
        } else {
            action = new InterceptRestAction(that.getHttpMethod(), that.getPath(),
                that.getQueryCount());
        }
        return canIntercept(action);
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        result = 31 * result + queryCount;
        return result;
    }
}
