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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import static java.lang.String.format;

public class Logger {
    public static final String DEBUG_OPTION = "gwtp.debug";

    private static final String SEE_LOG =
            "See previous log entries for details or compile with `-A" + DEBUG_OPTION + "` for additional details.";

    private final Messager messager;
    private final boolean debug;

    public Logger(
            Messager messager,
            Map<String, String> options) {
        this.messager = messager;
        this.debug = options.containsKey(DEBUG_OPTION);
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public void other(String message, Object... arguments) {
        other().log(message, arguments);
    }

    public LogBuilder other() {
        return new LogBuilder(this, Kind.OTHER);
    }

    public void debug(String message, Object... arguments) {
        debug().log(message, arguments);
    }

    public LogBuilder debug() {
        return new LogBuilder(this, Kind.OTHER, true);
    }

    public void note(String message, Object... arguments) {
        note().log(message, arguments);
    }

    public LogBuilder note() {
        return new LogBuilder(this, Kind.NOTE);
    }

    public void warning(String message, Object... arguments) {
        warning().log(message, arguments);
    }

    public LogBuilder warning() {
        return new LogBuilder(this, Kind.WARNING);
    }

    public void mandatoryWarning(String message, Object... arguments) {
        mandatoryWarning().log(message, arguments);
    }

    public LogBuilder mandatoryWarning() {
        return new LogBuilder(this, Kind.MANDATORY_WARNING);
    }

    public void error(String message, Object... arguments) {
        error().log(message, arguments);
    }

    public LogBuilder error() {
        return new LogBuilder(this, Kind.ERROR);
    }

    void debug(Kind kind, String message, Throwable throwable, Object[] arguments, Element element,
            AnnotationMirror annotationMirror, AnnotationValue annotationValue) {
        if (debug) {
            log(kind, "[DEBUG] " + message, throwable, arguments, element, annotationMirror, annotationValue);
        }
    }

    void log(Kind kind, String message, Throwable throwable, Object[] arguments, Element element,
            AnnotationMirror annotationMirror, AnnotationValue annotationValue) {
        String logMessage = message;
        if (!debug && (kind == Kind.ERROR || kind == Kind.MANDATORY_WARNING)) {
            logMessage += System.lineSeparator() + SEE_LOG;
        }

        messager.printMessage(kind, format(logMessage, arguments), element, annotationMirror, annotationValue);

        if (debug && throwable != null) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            messager.printMessage(Kind.ERROR, writer.toString());
        }
    }
}
