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

package com.gwtplatform.carstore.client.application.testutils;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

@SuppressWarnings("GwtInconsistentSerializableClass")
public class ActionImpl<T> implements RestAction<T> {
    private final TypeLiteral<RestAction<T>> typeLiteral;

    public ActionImpl(TypeLiteral<RestAction<T>> typeLiteral) {
        this.typeLiteral = typeLiteral;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    @Override
    public List<RestParameter> getPathParams() {
        return new ArrayList<RestParameter>();
    }

    @Override
    public List<RestParameter> getQueryParams() {
        return new ArrayList<RestParameter>();
    }

    @Override
    public List<RestParameter> getFormParams() {
        return new ArrayList<RestParameter>();
    }

    @Override
    public List<RestParameter> getHeaderParams() {
        return new ArrayList<RestParameter>();
    }

    @Override
    public Object getBodyParam() {
        return new Object();
    }

    @Override
    public Boolean hasFormParams() {
        return false;
    }

    @Override
    public Boolean hasBodyParam() {
        return false;
    }

    public TypeLiteral<RestAction<T>> getTypeLiteral() {
        return typeLiteral;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
