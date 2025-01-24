/*
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

package com.gwtplatform.dispatch.client;

import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * An implementation of {@link DispatchRequest} that is used in conjunction with {@link
 * com.gwtplatform.dispatch.client.interceptor.Interceptor Interceptor}s.
 * <p>
 * As the interceptor implementation may be provided asynchronously, the {@code DelegatingAsyncCallback} will initially
 * not contain a {@link DispatchRequest}. Once the interceptor is executed, this will be populated with a {@link
 * DispatchRequest} by calling {@link #setDelegate(DispatchRequest)}.
 */
public class DelegatingDispatchRequest implements DispatchRequest {
    private boolean cancelled;
    private DispatchRequest delegate;

    public DelegatingDispatchRequest() {
    }

    /**
     * Populates the {@link DelegatingDispatchRequest} object with a {@link DispatchRequest}.
     * <p>
     * If the code that requested the command to be executed has already chosen to cancel the {@link DispatchRequest},
     * the {@link DispatchRequest} that has been passed will be immediately cancelled.
     *
     * @param delegate The {@link DispatchRequest} object.
     */
    public void setDelegate(DispatchRequest delegate) {
        if (cancelled) {
            if (delegate != null) {
                this.delegate.cancel();
            }
        } else {
            if (delegate == null) {
                throw new NullPointerException("delegate");
            }
            if (this.delegate != null) {
                throw new RuntimeException("Delegate can only be set once.");
            }
            this.delegate = delegate;
        }
    }

    @Override
    public void cancel() {
        if (delegate != null) {
            delegate.cancel();
        } else {
            cancelled = true;
        }
    }

    @Override
    public boolean isPending() {
        if (delegate != null) {
            return delegate.isPending();
        } else {
            return !cancelled;
        }
    }
}
