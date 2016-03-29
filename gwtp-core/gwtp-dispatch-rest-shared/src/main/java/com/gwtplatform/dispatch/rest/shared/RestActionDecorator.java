/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.shared;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

public class RestActionDecorator<R> implements RestAction<R> {
    private final RestAction<R> action;
    private final List<HttpParameter> extraParams;

    public RestActionDecorator(
            RestAction<R> action,
            HttpParameter extraParam) {
        this(action, Collections.singletonList(extraParam));
    }

    public RestActionDecorator(
            RestAction<R> action,
            List<HttpParameter> extraParams) {
        this.action = action;
        this.extraParams = extraParams;
    }

    public RestAction<R> getDecoratedAction() {
        return action;
    }

    @Override
    public String getPath() {
        return action.getPath();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return action.getHttpMethod();
    }

    @Override
    public List<HttpParameter> getParameters(final Type type) {
        return FluentIterable.from(extraParams)
                .filter(parameter -> parameter.getType() == type)
                .append(action.getParameters(type))
                .toList();
    }

    @Override
    public Object getBodyParam() {
        return action.getBodyParam();
    }

    @Override
    public String getBodyClass() {
        return action.getBodyClass();
    }

    @Override
    public String getResultClass() {
        return action.getResultClass();
    }

    @Override
    public Boolean hasFormParams() {
        return action.hasFormParams();
    }

    @Override
    public Boolean hasBodyParam() {
        return action.hasBodyParam();
    }

    @Override
    public List<ContentType> getClientProducedContentTypes() {
        return action.getClientProducedContentTypes();
    }

    @Override
    public List<ContentType> getClientConsumedContentTypes() {
        return action.getClientConsumedContentTypes();
    }

    @Override
    public boolean isSecured() {
        return action.isSecured();
    }
}
