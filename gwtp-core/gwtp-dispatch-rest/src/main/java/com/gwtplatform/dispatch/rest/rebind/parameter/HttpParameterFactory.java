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

package com.gwtplatform.dispatch.rest.rebind.parameter;

import java.util.Collection;
import java.util.Date;

import javax.ws.rs.core.Cookie;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JAbstractMethod;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind.utils.JPrimitives;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.DateFormat;

import jakarta.inject.Inject;

// TODO: Ideally, resolvers would handle parameter validation and this class would delegate to them
public class HttpParameterFactory {
    private static final String MANY_REST_ANNOTATIONS =
            "`%s#%s` parameter's `%s` is annotated with more than one REST annotations.";
    private static final String DATE_FORMAT_NOT_DATE =
            "`%s#%s` parameter's `%s` is annotated with @DateFormat but its type is not Date.";
    private static final String COOKIE_BAD_RETURN_TYPE =
            "`%s#%s` parameter's `%s` is annotated with @CookieParam but has a bad return type. "
                    + "It may only be a primitive, Cookie or a Collection of Cookie.";
    private static final String NO_VALUE_RESOLVER = "Can't find a value resolver for `%s#%s`, parameter `%s`.";

    private final Logger logger;
    private final GeneratorContext context;
    private final HeaderParamValueResolver headerParamValueResolver;
    private final PathParamValueResolver pathParamValueResolver;
    private final QueryParamValueResolver queryParamValueResolver;
    private final MatrixParamValueResolver matrixParamValueResolver;
    private final FormParamValueResolver formParamValueResolver;
    private final CookieParamValueResolver cookieParamValueResolver;

    @Inject
    HttpParameterFactory(
            Logger logger,
            GeneratorContext context,
            HeaderParamValueResolver headerParamValueResolver,
            PathParamValueResolver pathParamValueResolver,
            QueryParamValueResolver queryParamValueResolver,
            MatrixParamValueResolver matrixParamValueResolver,
            FormParamValueResolver formParamValueResolver,
            CookieParamValueResolver cookieParamValueResolver) {
        this.logger = logger;
        this.context = context;
        this.headerParamValueResolver = headerParamValueResolver;
        this.pathParamValueResolver = pathParamValueResolver;
        this.queryParamValueResolver = queryParamValueResolver;
        this.matrixParamValueResolver = matrixParamValueResolver;
        this.formParamValueResolver = formParamValueResolver;
        this.cookieParamValueResolver = cookieParamValueResolver;
    }

    public HttpParameter create(JParameter parameter) {
        HttpParameterType type = findParameterType(parameter);
        String name = getResolverForType(type).resolve(parameter);
        String dateFormat = extractDateFormat(parameter);

        return new HttpParameter(parameter, type, name, dateFormat);
    }

    public boolean validate(JParameter parameter) {
        return validateAnnotationsCount(parameter)
                && validateDateFormat(parameter)
                && validateCookieParam(parameter)
                && validateResolverExistence(parameter);
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

    private HttpParamValueResolver<?> getResolverForType(HttpParameterType type) {
        switch (type) {
            case HEADER:
                return headerParamValueResolver;
            case PATH:
                return pathParamValueResolver;
            case QUERY:
                return queryParamValueResolver;
            case MATRIX:
                return matrixParamValueResolver;
            case FORM:
                return formParamValueResolver;
            case COOKIE:
                return cookieParamValueResolver;
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

    private boolean validateAnnotationsCount(JParameter parameter) {
        int annotationsCount = 0;
        for (HttpParameterType type : HttpParameterType.values()) {
            if (parameter.isAnnotationPresent(type.getAnnotationClass())) {
                ++annotationsCount;
            }
        }

        if (annotationsCount > 1) {
            error(parameter, MANY_REST_ANNOTATIONS);
        }

        return annotationsCount <= 1;
    }

    private boolean validateDateFormat(JParameter parameter) {
        boolean valid = !parameter.isAnnotationPresent(DateFormat.class)
                || Date.class.getCanonicalName().equals(parameter.getType().getQualifiedSourceName());

        if (!valid) {
            error(parameter, DATE_FORMAT_NOT_DATE);
        }

        return valid;
    }

    private boolean validateCookieParam(JParameter parameter) {
        boolean valid = true;

        if (parameter.isAnnotationPresent(HttpParameterType.COOKIE.getAnnotationClass())) {
            JType returnType = parameter.getType();
            String qualifiedSourceName = returnType.getQualifiedSourceName();

            // Basic return types
            valid = JPrimitives.isPrimitiveOrBoxed(returnType)
                    || Date.class.getName().equals(qualifiedSourceName)
                    || String.class.getName().equals(qualifiedSourceName)
                    || isCookie(returnType)
                    || isCookieCollection(returnType);
        }

        if (!valid) {
            error(parameter, COOKIE_BAD_RETURN_TYPE);
        }

        return valid;
    }

    private boolean isCookie(JType returnType) {
        JClassType returnClassType = returnType.isClassOrInterface();

        // NewCookie is not allowed in this context because interfaces may be shared by server
        // However, client code is encouraged to pass an instance of NewCookie.
        return returnClassType != null
                && Cookie.class.getName().equals(returnClassType.getQualifiedSourceName());
    }

    private boolean isCookieCollection(JType returnType) {
        JType collectionTypeArg = extractCollectionTypeArg(returnType);
        return collectionTypeArg != null && isCookie(collectionTypeArg);
    }

    private JType extractCollectionTypeArg(JType returnType) {
        JClassType collectionType = context.getTypeOracle().findType(Collection.class.getName());
        JParameterizedType returnParameterizedType = returnType.isParameterized();

        if (returnParameterizedType != null && collectionType != null) {
            JClassType[] typeArgs = returnParameterizedType.getTypeArgs();
            if (typeArgs != null && typeArgs.length == 1) {
                return typeArgs[0];
            }
        }

        return null;
    }

    private boolean validateResolverExistence(JParameter parameter) {
        HttpParameterType type = findParameterType(parameter);
        boolean exists = type != null;

        // null type may represent a body param.
        if (exists) {
            HttpParamValueResolver<?> resolver = getResolverForType(type);
            exists = resolver != null;

            // so we warn only if it haas a type and no resolver
            if (!exists) {
                error(parameter, NO_VALUE_RESOLVER);
            }
        }

        return exists;
    }

    private void error(JParameter parameter, String message) {
        JAbstractMethod method = parameter.getEnclosingMethod();
        String typeName = method.getEnclosingType().getQualifiedSourceName();
        String methodName = method.getName();
        String parameterName = parameter.getName();

        logger.error(String.format(message, typeName, methodName, parameterName));
    }
}
