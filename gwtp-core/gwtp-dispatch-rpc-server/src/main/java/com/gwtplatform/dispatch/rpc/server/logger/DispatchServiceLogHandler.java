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

import com.gwtplatform.dispatch.rpc.server.AbstractDispatchServiceImpl.DispatchType;
import com.gwtplatform.dispatch.rpc.shared.Action;

/**
 * Log handler for the {@link com.gwtplatform.dispatch.rpc.shared.DispatchService}. All the log statements and caught
 * Exceptions in the {@link com.gwtplatform.dispatch.rpc.shared.DispatchService} are send to this interface.
 *
 * @author Filip Hrisafov
 */
public interface DispatchServiceLogHandler {

    /**
     * A message that needs to be logged at info level.
     *
     * @param message to be logged
     */
    void info(String message);

    /**
     * Log a {@link Throwable} during the execution of an action or during undoing an action.
     *
     * @param throwable the throwable
     * @param action the action for which an exception occurred
     * @param type the type of the method in which the exception occurred
     */
    void log(Throwable throwable, Action<?> action, DispatchType type);

    /**
     * A message that needs to be logged at the severe level.
     *
     * @param message to be logged
     */
    void severe(String message);
}
