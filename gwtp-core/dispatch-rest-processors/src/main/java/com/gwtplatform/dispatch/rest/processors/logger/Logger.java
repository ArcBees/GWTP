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

package com.gwtplatform.dispatch.rest.processors.logger;

import java.util.Map;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;

public class Logger {
    public static final String DEBUG_OPTION = "debug";

    private final Messager messager;
    private final boolean debug;

    public Logger(
            Messager messager,
            Map<String, String> options) {
        this.messager = messager;
        this.debug = options.containsKey(DEBUG_OPTION);
    }

    public boolean debugEnabled() {
        return debug;
    }

    public void other(String message, Object... arguments) {
        messager.printMessage(Kind.OTHER, format(message, arguments));
    }

    public void debug(String message, Object... arguments) {
        if (debug) {
            messager.printMessage(Kind.NOTE, format(message, arguments));
        }
    }

    public void note(String message, Object... arguments) {
        messager.printMessage(Kind.NOTE, format(message, arguments));
    }

    public void warning(String message, Object... arguments) {
        messager.printMessage(Kind.WARNING, format(message, arguments));
    }

    public void warning(String message, Throwable throwable, Object... arguments) {
        messager.printMessage(Kind.WARNING, format(message, arguments));

        printStackTrace(throwable);
    }

    public void mandatoryWarning(String message, Object... arguments) {
        messager.printMessage(Kind.MANDATORY_WARNING, format(message, arguments));
    }

    public void mandatoryWarning(String message, Throwable throwable, Object... arguments) {
        messager.printMessage(Kind.MANDATORY_WARNING, format(message, arguments));

        printStackTrace(throwable);
    }

    public void error(String message, Object... arguments) {
        messager.printMessage(Kind.ERROR, format(message, arguments));
    }

    public void error(String message, Throwable throwable, Object... arguments) {
        messager.printMessage(Kind.ERROR, format(message, arguments));

        printStackTrace(throwable);
    }

    private String format(String message, Object[] arguments) {
        return String.format(message, arguments);
    }

    private void printStackTrace(Throwable throwable) {
        if (debug) {
            throwable.printStackTrace();
        }
    }
}
