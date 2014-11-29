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

package com.gwtplatform.dispatch.rest.rebind;

import java.lang.annotation.Annotation;

import com.google.gwt.core.ext.typeinfo.HasAnnotations;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

/**
 * Represents the HTTP methods supported by the {@link com.gwtplatform.dispatch.rest.client.RestDispatch RestDispatch}.
 */
public enum HttpVerb {
    GET(HttpMethod.GET, javax.ws.rs.GET.class),
    POST(HttpMethod.POST, javax.ws.rs.POST.class),
    PUT(HttpMethod.PUT, javax.ws.rs.PUT.class),
    DELETE(HttpMethod.DELETE, javax.ws.rs.DELETE.class),
    HEAD(HttpMethod.HEAD, javax.ws.rs.HEAD.class);

    private final HttpMethod verb;
    private final Class<? extends Annotation> annotationClass;

    HttpVerb(
            HttpMethod verb,
            Class<? extends Annotation> annotationClass) {
        this.verb = verb;
        this.annotationClass = annotationClass;
    }

    public static boolean isHttpMethod(HasAnnotations hasAnnotations) {
        for (HttpVerb type : values()) {
            if (hasAnnotations.isAnnotationPresent(type.getAnnotationClass())) {
                return true;
            }
        }

        return false;
    }

    public HttpMethod getVerb() {
        return verb;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}
