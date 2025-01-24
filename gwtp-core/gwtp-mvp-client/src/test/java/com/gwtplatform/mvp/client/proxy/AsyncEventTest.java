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

package com.gwtplatform.mvp.client.proxy;

import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Injector;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.mvp.client.proxy.AsyncEventPresenterTestUtil.MyAction;
import com.gwtplatform.mvp.client.proxy.AsyncEventPresenterTestUtil.MyProxy;
import com.gwtplatform.mvp.client.proxy.AsyncEventPresenterTestUtil.MyResult;
import com.gwtplatform.mvp.client.proxy.AsyncEventPresenterTestUtil.MyView;
import com.gwtplatform.tester.TestDispatchAsync;
import com.gwtplatform.tester.TestDispatchService;

import jakarta.inject.Inject;

import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Tests (using {@link AsyncEventPresenterTestUtil}) the correct firing and
 * handling of {@link AsyncCallStartEvent}, {@link AsyncCallSucceedEvent} and
 * {@link AsyncCallFailEvent}.
 */
@RunWith(JukitoRunner.class)
public class AsyncEventTest {

    @Inject
    MyView view;
    @Inject
    MyProxy proxy;
    @Inject
    SimpleEventBus eventBus;
    @Inject
    TestDispatchService service;
    @Inject
    Injector injector;

    @Test
    public void shouldSetLoadingAndNullOnSuccessfulServerCall() {
        // given
        TestDispatchAsync spy = spy(new TestDispatchAsync(service, injector));
        AsyncEventPresenterTestUtil presenter = new AsyncEventPresenterTestUtil(eventBus, view, proxy, spy);
        willAnswer(new GetAsynchronousAnswer()).given(spy).execute(eq(new MyAction()),
                ArgumentMatchers.<AsyncCallback<MyResult>>any());

        // when
        presenter.onBind();
        presenter.executeAsync();

        // then
        verify(view).setMessage("Loading...");
        verify(view).setMessage(null);
    }

    @Test
    public void shouldSetLoadingAndFailureOnFailedServerCall() {
        // given
        TestDispatchAsync spy = spy(new TestDispatchAsync(service, injector));
        AsyncEventPresenterTestUtil presenter = new AsyncEventPresenterTestUtil(eventBus, view, proxy, spy);
        willAnswer(new GetAsynchronousFailureAnswer()).given(spy).execute(eq(new MyAction()),
                ArgumentMatchers.<AsyncCallback<MyResult>>any());

        // when
        presenter.onBind();
        presenter.executeAsync();

        // then
        verify(view).setMessage("Loading...");
        verify(view).setMessage(null);
    }

    /**
     * A mock {@link Answer} which will allways call
     * {@link AsyncCallback#onSuccess(Object)} method.
     */
    private static final class GetAsynchronousAnswer implements Answer<Void> {
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Void answer(InvocationOnMock invocation) throws Throwable {
            final AsyncCallback callback = (AsyncCallback) invocation.getArguments()[1];
            callback.onSuccess(invocation.getArguments()[0]);
            return null;
        }
    }

    /**
     * A mock {@link Answer} which will allways call
     * {@link AsyncCallback#onFailure(Throwable)} method.
     */
    private static final class GetAsynchronousFailureAnswer implements Answer<Void> {
        @Override
        @SuppressWarnings({"rawtypes"})
        public Void answer(InvocationOnMock invocation) throws Throwable {
            final AsyncCallback callback = (AsyncCallback) invocation.getArguments()[1];
            callback.onFailure(new IllegalStateException());
            return null;
        }
    }
}
