/*
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

package com.gwtplatform.dispatch.rest.processors.definitions;

import java.util.Collection;

import com.google.common.base.MoreObjects;

public class HttpVariableDefinition implements HasImports {
    private final TypeDefinition type;
    private final String name;
    private final HttpAnnotationDefinition httpAnnotation;
    private final String dateFormat;
    private final boolean body;

    public HttpVariableDefinition(
            TypeDefinition type,
            String name,
            HttpAnnotationDefinition httpAnnotation,
            String dateFormat,
            boolean body) {
        this.type = type;
        this.name = name;
        this.httpAnnotation = httpAnnotation;
        this.dateFormat = dateFormat;
        this.body = body;
    }

    public TypeDefinition getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public HttpAnnotationDefinition getHttpAnnotation() {
        return httpAnnotation;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public boolean isBody() {
        return body;
    }

    @Override
    public Collection<String> getImports() {
        return type.getImports();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("name", name)
                .add("httpAnnotation", httpAnnotation)
                .add("dateFormat", dateFormat)
                .add("body", body)
                .add("imports", getImports())
                .toString();
    }
}
