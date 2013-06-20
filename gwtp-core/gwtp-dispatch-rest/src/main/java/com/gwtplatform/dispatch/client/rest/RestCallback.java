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

package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Use this callback if you need to retrieve the {@link Response} returned by the HTTP call.
 *
 * @see #getResponse()
 */
public abstract class RestCallback<T> implements AsyncCallback<T> {
    private Response response;

    void setResponse(Response response) {
        this.response = response;
    }

    /**
     * Retrieves the {@link Response} returned by the server.
     *
     * @return The {@link Response} returned by the HTTP call or {@code null} if the call failed before reaching the
     *         server.
     * @see #hasResponse()
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Utility methods to verify if a {@link Response} object is available.
     *
     * @return {@code true} if a {@link Response} object is available, {@code false} otherwise.
     * @see #getResponse() if a {@link Response} object is available.
     */
    public Boolean hasResponse() {
        return response != null;
    }
}
