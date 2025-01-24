/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of utility methods.
 */
public class Utils {

    /**
     * Logs an entire stack trace to the logger, so that it can easily be viewed in the App Engine log. The exception
     * will be logged as a severe error.
     * <p>
     * See also {@link #logStackTrace(Logger, Level, Throwable)}.
     *
     * @param log The {@link Logger} to use.
     * @param e   The {@link Exception} with the stack trace to log.
     */
    public static void logStackTrace(Logger log, Throwable e) {
        logStackTrace(log, Level.SEVERE, e);
    }

    /**
     * Logs an entire stack trace to the logger, so that it can easily be viewed in the App Engine log.
     *
     * @param log   The {@link Logger} to use.
     * @param level The {@link Level} at which to log the stack trace.
     * @param e     The {@link Exception} with the stack trace to log.
     */
    public static void logStackTrace(Logger log, Level level, Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        log.log(level, stringWriter.toString());
    }

}
