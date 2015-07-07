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

import javax.lang.model.element.ExecutableElement;

import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.isAnnotationPresent;

public class HttpVerbResolver {
    private static final String INVALID_VERB_COUNT = "End-Point method detected with *%s* HTTP verb annotations. "
            + "Verify that `%s#%s` is annotated with a single HTTP verb.";

    private final Logger logger;

    public HttpVerbResolver(Logger logger) {
        this.logger = logger;
    }

    public boolean canResolve(ExecutableElement element) {
        int annotationsCount = 0;

        for (HttpVerb annotation : HttpVerb.values()) {
            if (isAnnotationPresent(element, annotation.getAnnotationClass())) {
                annotationsCount += 1;
            }
        }

        boolean hasOneAnnotation = annotationsCount == 1;
        if (!hasOneAnnotation) {
            logger.warning().context(element).log(INVALID_VERB_COUNT, annotationsCount,
                    asType(element.getEnclosingElement()).getQualifiedName(), element.getSimpleName());
        }

        return hasOneAnnotation;
    }

    public HttpVerb resolve(ExecutableElement element) {
        for (HttpVerb verb : HttpVerb.values()) {
            if (isAnnotationPresent(element, verb.getAnnotationClass())) {
                return verb;
            }
        }

        return null;
    }
}
