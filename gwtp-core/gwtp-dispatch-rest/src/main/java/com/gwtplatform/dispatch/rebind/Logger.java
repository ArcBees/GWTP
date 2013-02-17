/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;

public final class Logger {
    private final TreeLogger treeLogger;

    public Logger(TreeLogger treeLogger) {
        this.treeLogger = treeLogger;
    }

    public void debug(String message, Object... params) {
        internalLog(TreeLogger.DEBUG, String.format(message, params));
    }

    public void info(String message, Object... params) {
        internalLog(TreeLogger.INFO, String.format(message, params));
    }

    public void warn(String message, Object... params) {
        internalLog(TreeLogger.WARN, String.format(message, params));
    }

    public TreeLogger getTreeLogger() {
        return treeLogger;
    }

    /**
     * Post an error message and halt processing. This method always throws an
     * {@link com.google.gwt.core.ext.UnableToCompleteException}
     */
    public void die(String message) throws UnableToCompleteException {
        internalLog(TreeLogger.ERROR, message);
        throw new UnableToCompleteException();
    }

    private void internalLog(Type type, String message) {
        treeLogger.log(type, message);
    }
}
