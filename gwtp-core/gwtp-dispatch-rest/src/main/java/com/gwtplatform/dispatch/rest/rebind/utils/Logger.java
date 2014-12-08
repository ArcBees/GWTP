/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public class Logger extends TreeLogger {
    private final TreeLogger treeLogger;

    public Logger(
            TreeLogger treeLogger) {
        this.treeLogger = treeLogger;
    }

    @Override
    public TreeLogger branch(Type type, String msg, Throwable caught, HelpInfo helpInfo) {
        return treeLogger.branch(type, msg, caught, helpInfo);
    }

    @Override
    public boolean isLoggable(Type type) {
        return treeLogger.isLoggable(type);
    }

    @Override
    public void log(Type type, String msg, Throwable caught, HelpInfo helpInfo) {
        treeLogger.log(type, msg, caught, helpInfo);
    }

    public void spam(String message, Object... params) {
        String format = String.format(message, params);
        treeLogger.log(TreeLogger.SPAM, format);
    }

    public void debug(String message, Object... params) {
        String format = String.format(message, params);
        treeLogger.log(TreeLogger.DEBUG, format);
    }

    public void trace(String message, Object... params) {
        String format = String.format(message, params);
        treeLogger.log(TreeLogger.TRACE, format);
    }

    public void info(String message, Object... params) {
        String format = String.format(message, params);
        treeLogger.log(TreeLogger.INFO, format);
    }

    public void warn(String message, Object... params) {
        warn(message, null, params);
    }

    public void warn(String message, Throwable caught, Object... params) {
        formatAndLog(TreeLogger.WARN, message, caught, params);
    }

    public void error(String message, Object... params) {
        error(message, null, params);
    }

    public void error(String message, Throwable caught, Object... params) {
        formatAndLog(TreeLogger.ERROR, message, caught, params);
    }

    /**
     * Post an error message and halt processing. This method always throws an {@link
     * com.google.gwt.core.ext.UnableToCompleteException UnableToCompleteException}.
     */
    public void die(String message, Object... params) throws UnableToCompleteException {
        die(message, null, params);
    }

    /**
     * Post an error message and halt processing. This method always throws an {@link
     * com.google.gwt.core.ext.UnableToCompleteException UnableToCompleteException}.
     */
    public void die(String message, Throwable caught, Object... params) throws UnableToCompleteException {
        error(message, caught, params);
        throw new UnableToCompleteException();
    }

    public void formatAndLog(Type type, String message, Throwable caught, Object... params) {
        String format = String.format(message, params);
        treeLogger.log(type, format, caught);
    }
}
