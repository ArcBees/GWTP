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

import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * An implementation of {@link DispatchRequest} that is always completed. It should be used with
 * {@code com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler}s that do not perform
 * any asynchronous processing.
 */
public class CompletedDispatchRequest implements DispatchRequest {
    public CompletedDispatchRequest() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isPending() {
        return false;
    }
}
