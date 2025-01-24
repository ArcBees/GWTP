/*
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

package com.gwtplatform.dispatch.rest.client.interceptor;

import java.util.List;
import java.util.Objects;

import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Context class used for the Rest Interceptor mappings.<br>
 * <pre>
 *     new InterceptorContext("/items", HttpMethod.GET, 2);
 * or:
 *     new InterceptorContext(getAction());
 * </pre>
 */
public class InterceptorContext {
    /**
     * {@link InterceptorContext} Builder.
     */
    public static class Builder {
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

        /**
         * Constructs {@link InterceptorContext} builder.
         */
        public Builder() {
        }

        /**
         * Constructs {@link InterceptorContext} builder.
         *
         * @param template the {@link RestAction} used as a template.
         */
        public Builder(RestAction<?> template) {
            this.template = template;
        }

        /**
         * Explicitly provide a REST path string.
         *
         * @param path the rest path.
         *
         * @return this {@link Builder} object.
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Explicitly provide an HttpMethod.
         *
         * @param httpMethod the REST {@link HttpMethod}.
         *
         * @return this {@link Builder} object.
         */
        public Builder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        /**
         * Explicitly provide a query count.
         *
         * @param queryCount the query count.
         *
         * @return this {@link Builder} object.
         */
        public Builder queryCount(int queryCount) {
            this.queryCount = queryCount;
            return this;
        }

        /**
         * Allow for transcendent context mapping.
         *
         * @param transcendent Use a transcendent strategy on the path, e.g. /path will be detected using /path/2.
         *
         * @return this {@link Builder} object.
         */
        public Builder transcendent(boolean transcendent) {
            this.transcendent = transcendent;
            return this;
        }

        /**
         * Any {@link HttpMethod} for context mapping.
         *
         * @param anyHttpMethod Allow any HTTP httpMethod when checking.
         *
         * @return this {@link Builder} object.
         */
        public Builder anyHttpMethod(boolean anyHttpMethod) {
            this.anyHttpMethod = anyHttpMethod;
            return this;
        }

        /**
         * Any query count for context mapping.
         *
         * @param anyQueryCount true allows any query param count.
         *
         * @return this {@link Builder} object.
         */
        public Builder anyQueryCount(boolean anyQueryCount) {
            this.anyQueryCount = anyQueryCount;
            return this;
        }

        /**
         * Build the {@link InterceptorContext}.
         *
         * @return built context.
         */
        public InterceptorContext build() {
            return new InterceptorContext(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Builder other = (Builder) obj;
            return anyHttpMethod == other.anyHttpMethod && anyQueryCount == other.anyQueryCount
                    && httpMethod == other.httpMethod && Objects.equals(path, other.path)
                    && queryCount == other.queryCount && Objects.equals(template, other.template)
                    && transcendent == other.transcendent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(anyHttpMethod, anyQueryCount, httpMethod, path, queryCount, template, transcendent);
        }
    }

    private Builder builder;

    protected InterceptorContext(Builder builder) {
        assert builder != null;
        this.builder = builder;

        if (builder.httpMethod == null) {
            builder.anyHttpMethod = true;
        }
        if (builder.queryCount < 0) {
            builder.anyQueryCount = true;
        }
    }

    protected boolean canIntercept(RestAction<?> action) {
        String path = builder.path;
        HttpMethod httpMethod = builder.httpMethod;
        int queryCount = builder.queryCount;

        // Required check types
        if (useTemplate()) {
            path = builder.template.getPath();
            httpMethod = builder.template.getHttpMethod();
            queryCount = builder.template.getParameters(Type.QUERY).size();
        }

        // Path Check
        if (isTranscendent()) {
            if (!action.getPath().startsWith(path)) {
                return false;
            }
        } else if (!action.getPath().equals(path)) {
            return false;
        }

        // Http Method Check
        if (!isAnyHttpMethod()) {
            if (!action.getHttpMethod().equals(httpMethod)) {
                return false;
            }
        }

        // Query Parameters
        if (!builder.anyQueryCount) {
            List<HttpParameter> queryParams = action.getParameters(Type.QUERY);
            if (queryParams.size() != queryCount) {
                return false;
            } else if (useTemplate()) {
                // We can do some thorough checking with templates
                if (!queryParams.equals(builder.template.getParameters(Type.QUERY))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean useTemplate() {
        return builder.template != null;
    }

    private boolean isAnyHttpMethod() {
        return builder.anyHttpMethod;
    }

    public String getPath() {
        return builder.path;
    }

    public HttpMethod getHttpMethod() {
        return builder.httpMethod;
    }

    public int getQueryCount() {
        return builder.queryCount;
    }

    public RestAction<?> getTemplate() {
        return builder.template;
    }

    public boolean isTranscendent() {
        return builder.transcendent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InterceptorContext that = (InterceptorContext) o;
        RestAction<?> action;
        if (that.useTemplate()) {
            action = that.getTemplate();
        } else {
            action = new InterceptorContextRestAction(that.getHttpMethod(), that.getPath(), that.getQueryCount());
        }
        return canIntercept(action);
    }

    @Override
    public int hashCode() {
        return builder.hashCode();
    }
}
