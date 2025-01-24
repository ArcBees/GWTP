/*
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

package com.gwtplatform.dispatch.rpc.client.interceptor;

/**
 * This exception is thrown as a side-effect of an error when registering a delegating callback. A provider was
 * registered to provide a client-side interceptor for an action, but this action was not the same action as specified
 * by {@code Interceptor#getActionType()}.
 */
public class InterceptorMismatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Class<?> requestedActionType;
    private final Class<?> supportedActionType;

    public InterceptorMismatchException(
            Class<?> requestedActionType,
            Class<?> supportedActionType) {
        this.requestedActionType = requestedActionType;
        this.supportedActionType = supportedActionType;
    }

    @SuppressWarnings("unused")
    private InterceptorMismatchException() {
        this(null, null);
    }

    public Class<?> getRequestedActionType() {
        return this.requestedActionType;
    }

    public Class<?> getSupportedActionType() {
        return this.supportedActionType;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " [requestedActionType=" + this.requestedActionType +
                ", supportedActionType=" + this.supportedActionType + "]";
    }
}
