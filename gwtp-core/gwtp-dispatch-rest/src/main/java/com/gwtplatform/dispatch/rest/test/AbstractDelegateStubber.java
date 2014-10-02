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

import javax.ws.rs.core.Response.Status;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Common stubbing context for a {@link com.gwtplatform.dispatch.rest.client.ResourceDelegate ResourceDelegate} call.
 *
 * @param <R> a resource type.
 */
public abstract class AbstractDelegateStubber<R, S extends AbstractDelegateStubber> {
    private final DelegateMocking<R> delegateMocking;

    private Response response;
    private DispatchRequest dispatchRequest;

    AbstractDelegateStubber(DelegateMocking<R> delegateMocking) {
        this.delegateMocking = delegateMocking;
    }

    /**
     * Syntactic sugar.
     *
     * @return this stubbing context.
     */
    public S and() {
        return self();
    }

    /**
     * Specify the {@link Response} that will be assigned to
     * {@link com.gwtplatform.dispatch.rest.client.RestCallback#setResponse(Response)
     * RestCallback#setResponse(Response)} when the call represented by the current context is performed.
     *
     * @param response the result object passed to the {@link com.gwtplatform.dispatch.rest.client.RestCallback
     * RestCallback}.
     *
     * @return this stubbing context.
     */
    public S withResponse(Response response) {
        this.response = response;
        return self();
    }

    /**
     * This is similar than calling {@link #withResponse(Response)}. Instead, a mock of {@link Response} is created and
     * configured to return <code>status</code>.
     * <p/>
     * If {@link #withResponse(Response)} was previously called, the previously configured {@link Response} will be
     * overwritten.
     *
     * @param status the {@link Status} that should be returned by the response.
     *
     * @return this stubbing context.
     */
    public S withStatus(Status status) {
        response = mock(Response.class);
        given(response.getStatusCode()).willReturn(status.getStatusCode());

        return self();
    }

    /**
     * Specify the {@link DispatchRequest} that will be assigned to the
     * {@link com.gwtplatform.dispatch.client.DelegatingDispatchRequest DelegatingDispatchRequest} that was passed to
     * {@link com.gwtplatform.dispatch.rest.client.ResourceDelegate#withDelegatingDispatchRequest(
     * com.gwtplatform.dispatch.client.DelegatingDispatchRequest) ResourceDelegate#withDelegatingDispatchRequest()}.
     *
     * @param response the {@link DispatchRequest} that will be passed to the configured {@link
     * com.gwtplatform.dispatch.client.DelegatingDispatchRequest DelegatingDispatchRequest}.
     *
     * @return this stubbing context.
     */
    public S withDispatchRequest(DispatchRequest dispatchRequest) {
        this.dispatchRequest = dispatchRequest;
        return self();
    }

    /**
     * Return a stubber based on the underlying resource. You will then call the method(s) you expect to be called on
     * your resource.
     *
     * @return The stubbing context of the underlying resource.
     */
    public R when() {
        return when(delegateMocking.getResource());
    }

    /**
     * Return a stubber based on <code>mock</code>. You will then call the method(s) you expect to be called on your
     * mock.
     *
     * @return The stubbing context of <code>mock</code>.
     */
    public <T> T when(T mock) {
        return doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                updateResponse();
                updateDispatchRequest();
                updateCallback(delegateMocking.getCallback());

                return null;
            }
        }).when(mock);
    }

    protected abstract void updateCallback(AsyncCallback callback);

    protected abstract S self();

    private void updateResponse() {
        if (response != null) {
            delegateMocking.getRestCallback().setResponse(response);
        }
    }

    private void updateDispatchRequest() {
        if (dispatchRequest != null) {
            delegateMocking.getDelegatingDispatchRequest().setDelegate(dispatchRequest);
        }
    }
}
