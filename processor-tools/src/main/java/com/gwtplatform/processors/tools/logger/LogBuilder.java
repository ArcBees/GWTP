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

package com.gwtplatform.processors.tools.logger;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class LogBuilder {
    private final Logger logger;
    private final Kind kind;
    private final boolean debug;

    private Throwable throwable;
    private Element element;
    private AnnotationMirror annotationMirror;
    private AnnotationValue annotationValue;

    LogBuilder(
            Logger logger,
            Kind kind) {
        this(logger, kind, false);
    }

    LogBuilder(
            Logger logger,
            Kind kind,
            boolean debug) {
        this.logger = logger;
        this.kind = kind;
        this.debug = debug;
    }

    public LogBuilder throwable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public LogBuilder context(Element element) {
        return context(element, null);
    }

    public LogBuilder context(Element element, AnnotationMirror annotationMirror) {
        return context(element, annotationMirror, null);
    }

    public LogBuilder context(Element element, AnnotationMirror annotationMirror, AnnotationValue annotationValue) {
        this.element = element;
        this.annotationMirror = annotationMirror;
        this.annotationValue = annotationValue;

        return this;
    }

    public void log(String message, Object... arguments) {
        if (debug) {
            logger.debug(kind, message, throwable, arguments, element, annotationMirror, annotationValue);
        } else {
            logger.log(kind, message, throwable, arguments, element, annotationMirror, annotationValue);
        }
    }
}
