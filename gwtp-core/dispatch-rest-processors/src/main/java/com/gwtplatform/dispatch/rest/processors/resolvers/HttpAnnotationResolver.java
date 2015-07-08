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

import com.gwtplatform.dispatch.rest.processors.definitions.HttpAnnotationDefinition;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.resolvers.parameters.HttpParamValueResolver;
import com.gwtplatform.dispatch.rest.processors.utils.Utils;

import static com.gwtplatform.dispatch.rest.processors.NameFactory.parentName;

public class HttpAnnotationResolver {
    private static final String MANY_REST_ANNOTATIONS =
            "Method `%s` parameter's `%s` is annotated with more than one REST annotations.";

    private final Logger logger;
    private final Iterable<HttpParamValueResolver> resolvers;

    public HttpAnnotationResolver(
            Logger logger,
            Utils utils) {
        this.logger = logger;
        this.resolvers = HttpParamValueResolver.getResolvers(logger, utils);
    }

    public boolean canResolve(VariableElement element) {
        int annotationsCount = 0;

        for (HttpParamValueResolver resolver : resolvers) {
            if (resolver.isPresent(element)) {
                resolver.canResolve(element);
                ++annotationsCount;
            }
        }

        if (annotationsCount > 1) {
            logger.error().context(element).log(MANY_REST_ANNOTATIONS, parentName(element), element.getSimpleName());
        }

        return annotationsCount <= 1;
    }

    public HttpAnnotationDefinition resolve(VariableElement element) {
        for (HttpParamValueResolver resolver : resolvers) {
            if (resolver.isPresent(element)) {
                String name = resolver.resolve(element);
                return new HttpAnnotationDefinition(resolver.getAssociatedType(), name);
            }
        }

        return null;
    }
}
