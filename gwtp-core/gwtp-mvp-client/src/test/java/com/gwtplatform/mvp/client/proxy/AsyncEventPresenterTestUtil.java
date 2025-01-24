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

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rpc.shared.ActionImpl;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.mvp.MainPresenterTestUtil;

/**
 * This is the test presenter which implements {@link AsyncCallFailHandler},
 * {@link AsyncCallStartHandler} and {@link AsyncCallSucceedHandler} to capture
 * all events we want to test.
 */
public class AsyncEventPresenterTestUtil extends
        Presenter<AsyncEventPresenterTestUtil.MyView, AsyncEventPresenterTestUtil.MyProxy> implements
        AsyncCallFailHandler,
        AsyncCallStartHandler, AsyncCallSucceedHandler {

    private final DispatchAsync dispatcher;

    /**
     * Presenter's view.
     */
    public interface MyView extends View {
        void setMessage(String string);
    }

    /**
     * Presenter's proxy.
     */
    @ProxyStandard
    public interface MyProxy extends Proxy<MainPresenterTestUtil> {
    }

    public AsyncEventPresenterTestUtil(EventBus eventBus, MyView view, MyProxy proxy, DispatchAsync dispatcher) {
        super(eventBus, view, proxy, RevealType.Root);
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(addHandler(AsyncCallStartEvent.getType(), this));
        registerHandler(addHandler(AsyncCallSucceedEvent.getType(), this));
        registerHandler(addHandler(AsyncCallFailEvent.getType(), this));
    }

    void executeAsync() {
        NotifyingAsyncCallback<MyResult> callback = new NotifyingAsyncCallback<MyResult>(getEventBus()) {
            @Override
            protected void success(MyResult result) {
                // Nothing needed here, we only want to test successful event handling
            }
        };

        callback.prepare();
        // In contrast to JavaDoc for NotifyingAsyncCallback we need to call
        // checkLoading before executing the server call; otherwise the server call
        // will return before checkLoading could be executed.
        callback.checkLoading();
        dispatcher.execute(new MyAction(), callback);
    }

    @Override
    public void onAsyncCallSucceed(AsyncCallSucceedEvent asyncCallSucceedEvent) {
        getView().setMessage(null);
    }

    @Override
    public void onAsyncCallStart(AsyncCallStartEvent asyncCallStartEvent) {
        getView().setMessage("Loading...");
    }

    @Override
    public void onAsyncCallFail(AsyncCallFailEvent asyncCallFailEvent) {
        getView().setMessage("Oops, something went wrong...");
    }

    static class MyAction extends ActionImpl<MyResult> {
        MyAction() {
        }
    }

    static class MyResult implements Result {
        private static final long serialVersionUID = 1L;

        private MyResult() {
        }
    }
}
