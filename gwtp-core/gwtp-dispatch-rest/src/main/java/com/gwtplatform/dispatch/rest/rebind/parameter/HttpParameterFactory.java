/**
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

package com.gwtplatform.dispatch.rest.rebind.parameter;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.google.gwt.core.ext.typeinfo.JAbstractMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.DateFormat;

public class HttpParameterFactory {
    private static final String MANY_REST_ANNOTATIONS =
            "`%s#s` parameter's `%s` is annotated with more than one REST " +
                    "annotations.";
    private static final String DATE_FORMAT_NOT_DATE =
            "`%s#s` parameter's `%s` is annotated with @DateFormat but its " +
                    "type is not Date";

    private final Logger logger;
    private final HeaderParamValueResolver headerParamValueResolver;
    private final PathParamValueResolver pathParamValueResolver;
    private final QueryParamValueResolver queryParamValueResolver;
    private final FormParamValueResolver formParamValueResolver;

    @Inject
    HttpParameterFactory(
            Logger logger,
            HeaderParamValueResolver headerParamValueResolver,
            PathParamValueResolver pathParamValueResolver,
            QueryParamValueResolver queryParamValueResolver,
            FormParamValueResolver formParamValueResolver) {
        this.logger = logger;
        this.headerParamValueResolver = headerParamValueResolver;
        this.pathParamValueResolver = pathParamValueResolver;
        this.queryParamValueResolver = queryParamValueResolver;
        this.formParamValueResolver = formParamValueResolver;
    }

    public HttpParameter create(
            JParameter parameter) {
        HttpParameterType type = findParameterType(parameter);
        String name = extractName(parameter, type);
        String dateFormat = extractDateFormat(parameter);

        return new HttpParameter(parameter, type, name, dateFormat);
    }

    public boolean validate(JParameter parameter) {
        int annotationsCount = 0;
        for (HttpParameterType type : HttpParameterType.values()) {
            if (parameter.isAnnotationPresent(type.getAnnotationClass())) {
                ++annotationsCount;
            }
        }

        // Those cases are errors, better stop and report now
        if (annotationsCount > 1) {
            error(parameter, MANY_REST_ANNOTATIONS);
        } else if (parameter.isAnnotationPresent(DateFormat.class) && !isDate(parameter)) {
            error(parameter, DATE_FORMAT_NOT_DATE);
        }

        // No annotations is not valid, but is valid for body params
        return annotationsCount == 1;
    }

    private HttpParameterType findParameterType(JParameter parameter) {
        HttpParameterType type = null;
        for (HttpParameterType enumType : HttpParameterType.values()) {
            if (parameter.isAnnotationPresent(enumType.getAnnotationClass())) {
                type = enumType;
            }
        }

        return type;
    }

    private String extractName(JParameter parameter, HttpParameterType type) {
        switch (type) {
            case HEADER:
                return headerParamValueResolver.resolve(parameter.getAnnotation(HeaderParam.class));
            case PATH:
                return pathParamValueResolver.resolve(parameter.getAnnotation(PathParam.class));
            case QUERY:
                return queryParamValueResolver.resolve(parameter.getAnnotation(QueryParam.class));
            case FORM:
                return formParamValueResolver.resolve(parameter.getAnnotation(FormParam.class));
            default:
                return null;
        }
    }

    private String extractDateFormat(JParameter parameter) {
        String format = null;
        if (parameter.isAnnotationPresent(DateFormat.class)) {
            format = parameter.getAnnotation(DateFormat.class).value();
        }

        return format;
    }

    private boolean isDate(JParameter parameter) {
        return Date.class.getCanonicalName().equals(parameter.getType().getQualifiedSourceName());
    }

    private void error(JParameter parameter, String message) {
        JAbstractMethod method = parameter.getEnclosingMethod();
        String typeName = method.getEnclosingType().getQualifiedSourceName();
        String methodName = method.getName();
        String parameterName = parameter.getName();

        logger.error(String.format(message, typeName, methodName, parameterName));
    }
}
