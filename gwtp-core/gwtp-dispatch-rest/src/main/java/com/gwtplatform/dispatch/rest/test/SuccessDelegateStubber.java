/**
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

package com.gwtplatform.dispatch.rest.test;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Stubbing context for a {@link com.gwtplatform.dispatch.rest.client.ResourceDelegate ResourceDelegate} call that
 * should fail.
 *
 * @param <R> a resource type.
 */
public class SuccessDelegateStubber<R> extends AbstractDelegateStubber<R, SuccessDelegateStubber> {
    private Object result;

    SuccessDelegateStubber(DelegateMocking<R> delegateMocking) {
        super(delegateMocking);
    }

    /**
     * Specify the result object that will be assigned to {@link AsyncCallback#onSuccess(Object)} when the call
     * represented by the current context is performed.
     *
     * @param result the result object passed to the {@link AsyncCallback}.
     *
     * @return this stubbing context.
     */
    public SuccessDelegateStubber<R> withResult(Object result) {
        this.result = result;
        return self();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateCallback(AsyncCallback callback) {
        callback.onSuccess(result);
    }

    @Override
    protected SuccessDelegateStubber<R> self() {
        return this;
    }
}
