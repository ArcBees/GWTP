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

package com.gwtplatform.dispatch.client;

/**
 * Implementations of this interface can be added to a com.gwtplatform.dispatch.shared.DispatchAsync
 * implementation to intercept exceptions which return from further up the chain.
 */
public interface ExceptionHandler {
    /**
     * {@link ExceptionHandler}'s status enumeration.
     */
    enum Status {
        CONTINUE, STOP
    }

    /**
     * This method is called when an exception occurs. Return {@link Status#STOP} to indicate that the exception has
     * been handled and further processing should not occur. Return {@link Status#CONTINUE} to indicate that further
     * processing should occur.
     *
     * @param e The exception.
     * @return The status after execution.
     */
    Status onFailure(Throwable e);
}
