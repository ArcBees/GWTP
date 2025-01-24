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

import com.google.gwt.http.client.Request;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * An implementation of {@link DispatchRequest} that is an adapter for {@link Request}.
 * <p>
 * If the code that requested the command to be executed chooses to cancel the {@link DispatchRequest} and the
 * {@link Request} that has been passed is still pending, it will be cancelled.
 */
public class GwtHttpDispatchRequest implements DispatchRequest {
    private final Request request;

    public GwtHttpDispatchRequest(Request request) {
        this.request = request;
    }

    @Override
    public void cancel() {
        request.cancel();
    }

    @Override
    public boolean isPending() {
        return request.isPending();
    }
}
