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

package com.gwtplatform.dispatch.rest.rebind2;

import java.lang.annotation.Annotation;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.google.gwt.core.ext.typeinfo.HasAnnotations;

public enum HttpParameterType {
    HEADER(HeaderParam.class),
    PATH(PathParam.class),
    QUERY(QueryParam.class),
    FORM(FormParam.class);

    private final Class<? extends Annotation> annotationClass;

    HttpParameterType(
            Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public static boolean isHttpParameter(HasAnnotations hasAnnotations) {
        for (HttpParameterType type : values()) {
            if (hasAnnotations.isAnnotationPresent(type.getAnnotationClass())) {
                return true;
            }
        }

        return false;
    }
}
