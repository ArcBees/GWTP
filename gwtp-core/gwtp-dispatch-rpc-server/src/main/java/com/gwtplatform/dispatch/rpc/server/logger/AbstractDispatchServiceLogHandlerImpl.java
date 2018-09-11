/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.server.logger;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gwtplatform.dispatch.rpc.server.AbstractDispatchServiceImpl.DispatchType;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.ServiceException;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * This is the server-side implementation of the {@link DispatchServiceLogHandler}. The implementation transfers the
 * logging to the underlying {@link Logger}. If you need different logging then instantiate your own {@link
 * DispatchServiceLogHandler}.
 *
 * @author Filip Hrisafov
 */
public class AbstractDispatchServiceLogHandlerImpl implements DispatchServiceLogHandler {

    private static final String ACTION_EXCEPTION_MSG = "Action exception while {0} {1}: {2}";
    private static final String SERVICE_EXCEPTION_MSG = "Service exception while {0} {1}: {2}";
    private static final String UNEXPECTED_EXCEPTION_MSG = "Unexpected exception while {0} {1}: {2}";

    private final Logger logger;

    protected AbstractDispatchServiceLogHandlerImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void severe(String message) {
        logger.severe(message);
    }

    @Override
    public void log(Throwable throwable, Action<?> action, DispatchType type) {
        if (throwable instanceof ActionException) {
            warn(throwable, action, type, ACTION_EXCEPTION_MSG);
        } else if (throwable instanceof ServiceException) {
            warn(throwable, action, type, SERVICE_EXCEPTION_MSG);
        } else {
            warn(throwable, action, type, UNEXPECTED_EXCEPTION_MSG);
        }
    }

    private void warn(Throwable throwable, Action<?> action, DispatchType type, String pattern) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, MessageFormat
                    .format(pattern, messageForDispatch(type), action.getClass().getName(), throwable.getMessage(),
                            throwable));
        }
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    protected String messageForDispatch(DispatchType type) {
        String message = "unknown";
        if (type != null) {
            switch (type) {
                case Execute:
                    message = "executing";
                    break;
                case Undo:
                    message = "undoing";
                    break;
            }
        }
        return message;
    }
}
