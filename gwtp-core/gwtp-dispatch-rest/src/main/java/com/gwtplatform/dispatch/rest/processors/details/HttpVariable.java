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

package com.gwtplatform.dispatch.rest.processors.details;

import java.util.Collection;

import javax.lang.model.element.VariableElement;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.resolvers.DateFormatResolver;
import com.gwtplatform.dispatch.rest.processors.resolvers.HttpAnnotationResolver;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.domain.Variable;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

public class HttpVariable implements HasImports {
    private final Type type;
    private final String name;
    private final Optional<HttpAnnotation> httpAnnotation;
    private final Optional<String> dateFormat;
    private final Optional<String> regex;

    public HttpVariable(
            Logger logger,
            Utils utils,
            PathDetails path,
            Variable variable) {
        VariableElement variableElement = variable.getElement();

        type = variable.getType();
        name = variable.getName();
        httpAnnotation = new HttpAnnotationResolver(logger, utils).resolve(variableElement);
        dateFormat = new DateFormatResolver(logger).resolve(variableElement);
        regex = findRegex(path);
    }

    private Optional<String> findRegex(PathDetails path) {
        if (!httpAnnotation.isPresent()) {
            return absent();
        }

        return fromNullable(path.getRegex(httpAnnotation.get().getName()));
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Optional<HttpAnnotation> getHttpAnnotation() {
        return httpAnnotation;
    }

    public Optional<String> getDateFormat() {
        return dateFormat;
    }

    public Optional<String> getRegex() {
        return regex;
    }

    public boolean isBody() {
        return !httpAnnotation.isPresent();
    }

    @Override
    public Collection<String> getImports() {
        return type.getImports();
    }
}
