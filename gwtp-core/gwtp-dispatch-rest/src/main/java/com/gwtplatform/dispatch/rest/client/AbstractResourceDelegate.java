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

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.dispatch.shared.DispatchRequest;

public abstract class AbstractResourceDelegate<T> implements ResourceDelegate<T>, Cloneable {
    private static final AsyncCallback<Object> NO_OP_CALLBACK = new AsyncCallback<Object>() {
        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(Object result) {
        }
    };

    protected final RestDispatch dispatcher;

    protected AsyncCallback<?> callback;
    protected DelegatingDispatchRequest delegatingDispatchRequest;

    protected AbstractResourceDelegate(RestDispatch dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public ResourceDelegate<T> withDelegatingDispatchRequest(DelegatingDispatchRequest delegatingDispatchRequest) {
        AbstractResourceDelegate<T> delegate = createCopy();
        delegate.delegatingDispatchRequest = delegatingDispatchRequest;

        return this;
    }

    @Override
    public T withoutCallback() {
        return withCallback(NO_OP_CALLBACK);
    }

    @Override
    public T withCallback(AsyncCallback<?> callback) {
        AbstractResourceDelegate<T> delegate = createCopy();
        delegate.callback = callback;

        return delegate.asResource();
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    protected <R> void execute(RestAction<R> action) {
        DispatchRequest dispatchRequest = dispatcher.execute(action, (AsyncCallback<R>) callback);

        if (delegatingDispatchRequest != null) {
            delegatingDispatchRequest.setDelegate(dispatchRequest);
        }
    }

    protected void copyFields(AbstractResourceDelegate<?> delegate) {
        delegate.delegatingDispatchRequest = delegatingDispatchRequest;
        delegate.callback = callback;
    }

    protected abstract AbstractResourceDelegate<T> newInstance();

    protected abstract T asResource();

    private AbstractResourceDelegate<T> createCopy() {
        AbstractResourceDelegate<T> delegate = newInstance();
        copyFields(delegate);

        return delegate;
    }
}
