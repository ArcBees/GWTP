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
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

/**
 * Provides a basic implementation of {@link RestAction} to inheritors. This is used by GWTP code-generator to create
 * the actions defined by the services.
 *
 * @param <R> the result type
 */
public abstract class AbstractRestAction<R> implements RestAction<R> {
    private String defaultDateFormat = DateFormat.DEFAULT;
    private HttpMethod httpMethod;
    private String rawServicePath;

    private List<RestParameter> pathParams = new ArrayList<RestParameter>();
    private List<RestParameter> headerParams = new ArrayList<RestParameter>();
    private List<RestParameter> queryParams = new ArrayList<RestParameter>();
    private List<RestParameter> formParams = new ArrayList<RestParameter>();

    private Object bodyParam;

    protected AbstractRestAction() {
    }

    protected AbstractRestAction(
            HttpMethod httpMethod,
            String rawServicePath,
            String defaultDateFormat) {
        this.httpMethod = httpMethod;
        this.rawServicePath = rawServicePath;
        this.defaultDateFormat = defaultDateFormat;
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
    public Object getBodyParam() {
        return bodyParam;
    }

    @Override
    public Boolean hasFormParams() {
        return !formParams.isEmpty();
    }

    @Override
    public Boolean hasBodyParam() {
        return bodyParam != null;
    }

    protected void addPathParam(String name, Object value) {
        pathParams.add(new RestParameter(name, value));
    }

    protected void addPathParam(String name, Date date) {
        addPathParam(name, date, defaultDateFormat);
    }

    protected void addPathParam(String name, Date date, String pattern) {
        String value = formatDate(date, pattern);
        addPathParam(name, value);
    }

    protected void addQueryParam(String name, Object value) {
        queryParams.add(new RestParameter(name, value));
    }

    protected void addQueryParam(String name, Date date) {
        addQueryParam(name, date, defaultDateFormat);
    }

    protected void addQueryParam(String name, Date date, String pattern) {
        String value = formatDate(date, pattern);
        addQueryParam(name, value);
    }

    protected void addFormParam(String name, Object value) {
        formParams.add(new RestParameter(name, value));
    }

    protected void addFormParam(String name, Date date) {
        addFormParam(name, date, defaultDateFormat);
    }

    protected void addFormParam(String name, Date date, String pattern) {
        String value = formatDate(date, pattern);
        addFormParam(name, value);
    }

    protected void addHeaderParam(String name, Object value) {
        headerParams.add(new RestParameter(name, value));
    }

    protected void addHeaderParam(String name, Date date) {
        addHeaderParam(name, date, defaultDateFormat);
    }

    protected void addHeaderParam(String name, Date date, String pattern) {
        String value = formatDate(date, pattern);
        addHeaderParam(name, value);
    }

    protected void setBodyParam(Object value) {
        bodyParam = value;
    }

    private String formatDate(Date date, String pattern) {
        return DateTimeFormat.getFormat(pattern).format(date);
    }
}
