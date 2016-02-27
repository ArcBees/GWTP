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

import com.gwtplatform.dispatch.rest.processors.details.HttpVerb;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.LogBuilder;
import com.gwtplatform.processors.tools.logger.Logger;

import static com.google.auto.common.MoreElements.isAnnotationPresent;

public class HttpVerbResolver {
    private static final String INVALID_VERB_COUNT = "End-Point method detected with *%s* HTTP verb annotations. "
            + "Verify that one and only one HTTP verb is present.";

    private final Logger logger;

    public HttpVerbResolver(Logger logger) {
        this.logger = logger;
    }

    public static boolean isPresent(ExecutableElement element) {
        for (HttpVerb annotation : HttpVerb.values()) {
            if (isAnnotationPresent(element, annotation.getAnnotationClass())) {
                return true;
            }
        }

        return false;
    }

    public HttpVerb resolve(ExecutableElement element) {
        int annotationsCount = 0;
        HttpVerb verb = null;

        for (HttpVerb annotation : HttpVerb.values()) {
            if (isAnnotationPresent(element, annotation.getAnnotationClass())) {
                annotationsCount += 1;
                verb = annotation;
            }
        }

        logPotentialErrors(element, annotationsCount);

        return verb;
    }

    public void logPotentialErrors(ExecutableElement element, int annotationsCount) {
        if (annotationsCount > 1) {
            LogBuilder logBuilder;
            if (annotationsCount > 1) {
                logBuilder = logger.warning();
            } else {
                logBuilder = logger.error();
            }

            logBuilder.context(element).log(INVALID_VERB_COUNT, annotationsCount);

            if (annotationsCount == 0) {
                throw new UnableToProcessException();
            }
        }
    }
}
