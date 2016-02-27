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

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.details.HttpAnnotation;
import com.gwtplatform.dispatch.rest.processors.resolvers.parameters.HttpParamValueResolver;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.dispatch.rest.processors.NameUtils.parentName;

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

    public Optional<HttpAnnotation> resolve(VariableElement element) {
        int annotationsCount = 0;
        Optional<HttpAnnotation> annotation = Optional.absent();

        for (HttpParamValueResolver resolver : resolvers) {
            if (resolver.isPresent(element)) {
                ++annotationsCount;
                annotation = Optional.of(new HttpAnnotation(resolver.getAssociatedType(), resolver.resolve(element)));
            }
        }

        if (annotationsCount > 1) {
            logger.warning().context(element).log(MANY_REST_ANNOTATIONS, parentName(element), element.getSimpleName());
        }

        return annotation;
    }
}
