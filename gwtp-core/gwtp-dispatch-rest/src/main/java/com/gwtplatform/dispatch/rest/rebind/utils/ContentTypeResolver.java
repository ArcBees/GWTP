/**
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gwt.core.ext.typeinfo.HasAnnotations;

public class ContentTypeResolver {
    private static final Set<ContentType> DEFAULT_FALLBACK;
    private static final Pattern MULTIPLE_CONTENT_TYPE_PATTERN = Pattern.compile("[,]");

    static {
        Set<ContentType> contentTypes = new HashSet<ContentType>();
        contentTypes.add(ContentType.valueOf(MediaType.WILDCARD));

        DEFAULT_FALLBACK = Collections.unmodifiableSet(contentTypes);
    }

    public static Set<ContentType> resolveConsumes(HasAnnotations hasAnnotations) {
        return resolveConsumes(hasAnnotations, DEFAULT_FALLBACK);
    }

    public static Set<ContentType> resolveConsumes(HasAnnotations hasAnnotations, Set<ContentType> fallback) {
        Consumes consumes = hasAnnotations.getAnnotation(Consumes.class);
        if (consumes != null && consumes.value().length > 0) {
            return resolveContentTypes(consumes.value(), fallback);
        }

        return fallback;
    }

    public static Set<ContentType> resolveProduces(HasAnnotations hasAnnotations) {
        return resolveProduces(hasAnnotations, DEFAULT_FALLBACK);
    }

    public static Set<ContentType> resolveProduces(HasAnnotations hasAnnotations, Set<ContentType> fallback) {
        Produces produces = hasAnnotations.getAnnotation(Produces.class);
        if (produces != null) {
            return resolveContentTypes(produces.value(), fallback);
        }

        return fallback;
    }

    private static Set<ContentType> resolveContentTypes(String[] values, Set<ContentType> fallback) {
        Set<ContentType> result = new HashSet<ContentType>(values.length);

        for (String value : values) {
            String[] parts = MULTIPLE_CONTENT_TYPE_PATTERN.split(value);
            for (String part : parts) {
                result.add(ContentType.valueOf(part));
            }
        }

        if (result.isEmpty()) {
            result.addAll(fallback);
        }

        return Collections.unmodifiableSet(result);
    }
}
