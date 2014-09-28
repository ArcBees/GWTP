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

package com.gwtplatform.dispatch.client.actionhandler;

/**
 * This exception is thrown as a side-effect of an error calling {@link DefaultClientActionHandlerRegistry#register}.
 * A provider was registered to provide a client-side action handler for an action, but this action was not the same
 * action as specified by {@link ClientActionHandler#getActionType()}.
 *
 * @deprecated use {@link com.gwtplatform.dispatch.client.interceptor.InterceptorMismatchException}
 */
@Deprecated
public class ClientActionHandlerMismatchException extends RuntimeException {
    private final Class<?> requestedActionType;
    private final Class<?> supportedActionType;

    public ClientActionHandlerMismatchException(Class<?> requestedActionType,
                                                Class<?> supportedActionType) {
        this.requestedActionType = requestedActionType;
        this.supportedActionType = supportedActionType;
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
