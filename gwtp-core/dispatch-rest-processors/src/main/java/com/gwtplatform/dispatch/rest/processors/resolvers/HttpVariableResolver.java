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

package com.gwtplatform.dispatch.rest.processors.resolvers;

import javax.lang.model.element.VariableElement;

import com.gwtplatform.dispatch.rest.processors.domain.HttpAnnotation;
import com.gwtplatform.dispatch.rest.processors.domain.HttpVariable;
import com.gwtplatform.dispatch.rest.processors.domain.Type;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.utils.Utils;

public class HttpVariableResolver {
    private final HttpAnnotationResolver httpAnnotationResolver;
    private final DateFormatResolver dateFormatResolver;

    public HttpVariableResolver(
            Logger logger,
            Utils utils) {
        this.httpAnnotationResolver = new HttpAnnotationResolver(logger, utils);
        this.dateFormatResolver = new DateFormatResolver(logger);
    }

    public boolean canResolve(VariableElement element) {
        return httpAnnotationResolver.canResolve(element)
                && dateFormatResolver.canResolve(element);
    }

    public HttpVariable resolve(VariableElement element) {
        Type type = new Type(element.asType());
        String name = element.getSimpleName().toString();
        HttpAnnotation httpAnnotation = httpAnnotationResolver.resolve(element);
        String dateFormat = dateFormatResolver.resolve(element);
        boolean body = httpAnnotation == null;

        return new HttpVariable(type, name, httpAnnotation, dateFormat, body);
    }
}
