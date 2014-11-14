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

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An {@link AsyncCallback} you can use if you need to retrieve the {@link Response} returned by the HTTP call.
 *
 * @param <R> Th result type
 * @see #setResponse(Response)
 */
public interface RestCallback<R> extends AsyncCallback<R> {
    /**
     * The {@link Response} received from the server. If the request did not reach the server, this method will not be
     * called. This method is called before {@link #onSuccess(Object)} and {@link #onFailure(Throwable)}.
     *
     * @param response The {@link Response} received from the server.
     */
    void setResponse(Response response);
}
