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

import org.mockito.ArgumentCaptor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.rest.client.ResourceDelegate;
import com.gwtplatform.dispatch.rest.client.RestCallback;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Stubbing context for a {@link ResourceDelegate}.
 *
 * @param <R> a resource type.
 */
public class DelegateMocking<R> {
    private final ResourceDelegate<R> delegate;

    private R resource;
    private ArgumentCaptor<AsyncCallback> callbackCaptor;
    private ArgumentCaptor<DelegatingDispatchRequest> delegatingDispatchRequestCaptor;

    DelegateMocking(ResourceDelegate<R> delegate) {
        this.delegate = delegate;
    }

    /**
     * Create a mock of <code>resourceClass</code> and associate the current delegate to it. Must be called only
     * <b>once</b> and <b>before</b> any other method is called.
     *
     * @param resourceClass the resourceClass to associate to the current delegate.
     *
     * @return this instance so you can start mocking the resource.
     */
    public DelegateMocking<R> useResource(Class<R> resourceClass) {
        return useResource(mock(resourceClass));
    }

    /**
     * Associate the current delegate to <code>resource</code>. Must be called only <b>once</b> and <b>before</b> any
     * other method is called.
     *
     * @param resource the resource to associate to the current delegate. Must be a mock.
     *
     * @return this instance so you can start mocking the resource.
     */
    public DelegateMocking<R> useResource(R resource) {
        assert this.resource == null
                : "useResource(R) called more than once. Did you forget to call DelegateTestUtils.init()?";

        this.resource = resource;
        callbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);
        delegatingDispatchRequestCaptor = ArgumentCaptor.forClass(DelegatingDispatchRequest.class);

        when(delegate.withCallback(callbackCaptor.capture())).thenReturn(resource);
        when(delegate.withDelegatingDispatchRequest(delegatingDispatchRequestCaptor.capture())).thenReturn(delegate);

        return this;
    }

    /**
     * Syntactic sugar.
     *
     * @return this instance
     */
    public DelegateMocking<R> and() {
        return this;
    }

    /**
     * Create a stubbing context for a {@link ResourceDelegate} call that should succeed.
     */
    public SuccessDelegateStubber<R> succeed() {
        verifyReadyToStub();

        return new SuccessDelegateStubber<R>(this);
    }

    /**
     * Create a stubbing context for a {@link ResourceDelegate} call that should fail.
     */
    public FailureDelegateStubber<R> fail() {
        verifyReadyToStub();

        return new FailureDelegateStubber<R>(this);
    }

    ResourceDelegate<R> getDelegate() {
        return delegate;
    }

    R getResource() {
        return resource;
    }

    DelegatingDispatchRequest getDelegatingDispatchRequest() {
        return delegatingDispatchRequestCaptor.getValue();
    }

    AsyncCallback getCallback() {
        return callbackCaptor.getValue();
    }

    RestCallback getRestCallback() {
        AsyncCallback callback = getCallback();

        assert callback instanceof RestCallback;
        return (RestCallback) callback;
    }

    private void verifyReadyToStub() {
        assert resource != null : "You must call useResource(R) before stubbing a delegate.";
    }
}
