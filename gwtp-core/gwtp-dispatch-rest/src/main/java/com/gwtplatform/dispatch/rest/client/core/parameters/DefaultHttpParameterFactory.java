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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.annotations.DefaultDateFormat;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import jakarta.inject.Inject;

public class DefaultHttpParameterFactory implements HttpParameterFactory {
    private final UrlUtils urlUtils;
    private final String defaultDateFormat;

    @Inject
    public DefaultHttpParameterFactory(
            UrlUtils urlUtils,
            @DefaultDateFormat String defaultDateFormat) {
        this.urlUtils = urlUtils;
        this.defaultDateFormat = defaultDateFormat;
    }

    @Override
    public HttpParameter create(Type type, String name, Object object) {
        return create(type, name, object, defaultDateFormat);
    }

    @Override
    public HttpParameter create(HttpParameter.Type type, String name, Object object, String dateFormat) {
        switch (type) {
            case FORM:
                return new FormParameter(name, object, dateFormat, urlUtils);
            case HEADER:
                return new HeaderParameter(name, object, dateFormat);
            case PATH:
                return new PathParameter(name, object, dateFormat, urlUtils);
            case QUERY:
                return new QueryParameter(name, object, dateFormat, urlUtils);
            case MATRIX:
                return new MatrixParameter(name, object, dateFormat, urlUtils);
            case COOKIE:
                return new CookieParameter(name, object, dateFormat);
            default:
                return new ClientHttpParameter(type, name, object, dateFormat);
        }
    }
}
