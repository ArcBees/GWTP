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
import com.gwtplatform.dispatch.rest.shared.DateFormat;
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
    private String defaultDateFormat;
    private HttpMethod httpMethod;
    private String rawServicePath;

    private List<RestParameter> pathParams;
    private List<RestParameter> headerParams;
    private List<RestParameter> queryParams;
    private List<RestParameter> formParams;

    private Object bodyParam;

    protected AbstractRestAction() {
        defaultDateFormat = DateFormat.DEFAULT;

        pathParams = new ArrayList<RestParameter>();
        headerParams = new ArrayList<RestParameter>();
        queryParams = new ArrayList<RestParameter>();
        formParams = new ArrayList<RestParameter>();
    }

    protected AbstractRestAction(
            HttpMethod httpMethod,
            String rawServicePath,
            String defaultDateFormat) {
        this();

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
        addParam(pathParams, name, value);
    }

    protected void addPathParam(String name, Date date) {
        addDateParam(pathParams, name, date);
    }

    protected void addPathParam(String name, Date date, String pattern) {
        addDateParam(pathParams, name, date, pattern);
    }

    protected void addQueryParam(String name, Object value) {
        addParam(queryParams, name, value);
    }

    protected void addQueryParam(String name, Date date) {
        addDateParam(queryParams, name, date);
    }

    protected void addQueryParam(String name, Date date, String pattern) {
        addDateParam(queryParams, name, date, pattern);
    }

    protected void addFormParam(String name, Object value) {
        addParam(formParams, name, value);
    }

    protected void addFormParam(String name, Date date) {
        addDateParam(formParams, name, date);
    }

    protected void addFormParam(String name, Date date, String pattern) {
        addDateParam(formParams, name, date, pattern);
    }

    protected void addHeaderParam(String name, Object value) {
        addParam(headerParams, name, value);
    }

    protected void addHeaderParam(String name, Date date) {
        addDateParam(headerParams, name, date);
    }

    protected void addHeaderParam(String name, Date date, String pattern) {
        addDateParam(headerParams, name, date, pattern);
    }

    protected void setBodyParam(Object value) {
        bodyParam = value;
    }

    protected String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        } else {
            return DateTimeFormat.getFormat(pattern).format(date);
        }
    }

    private void addDateParam(List<RestParameter> target, String name, Date date) {
        String value = formatDate(date, defaultDateFormat);
        addParam(target, name, value);
    }

    private void addDateParam(List<RestParameter> target, String name, Date date, String pattern) {
        String value = formatDate(date, pattern);
        addParam(target, name, value);
    }

    private void addParam(List<RestParameter> target, String name, Object value) {
        if (value != null) {
            target.add(new RestParameter(name, value));
        }
    }
}
