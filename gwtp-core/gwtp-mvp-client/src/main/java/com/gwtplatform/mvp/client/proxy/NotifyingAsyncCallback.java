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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * An implementation of {@link AsyncCallback} that sends events on the {@link EventBus} whenever
 * an async call starts, succeeds, or fails. The events fired are {@link AsyncCallStartEvent},
 * {@link AsyncCallSucceedEvent}, {@link AsyncCallFailEvent}.
 * <p>
 * So the way to use {@link NotifyingAsyncCallback} is the following:
 * <pre>
 *   NotifyingAsyncCallback callback = new NotifyingAsyncCallback(eventBus) { ... };
 *   callback.prepare();
 *   someRpcMethod(callback);
 *   callback.checkLoading();
 * </pre>
 *
 * @param <T> The type of the return value. See {@link AsyncCallback}.
 */
public abstract class NotifyingAsyncCallback<T> implements AsyncCallback<T> {

    /**
     * This enum indicates the state of the notifying async callback. It starts in
     * the {@code UNKNOWN} state and moves to {@code INITIALIZED} when {@link #prepare}
     * is called. If {@link #checkLoading} is called before the async call has succeeded
     * or failed, then it moves to {@code LOADING}. When the call terminates it moves to
     * {@code SUCCEEDED} or {@code FAILED}.
     */
    public enum State {
        UNKNOWN,
        INITIALIZED,
        LOADING,
        SUCCEEDED,
        FAILED
    }

    // Using a static counter to ensure we only fire once, otherwise we risk running into an
    // infinite loop if the handler requires an async call to fetch the presenter (which is the
    // case when using @ProxyEvent).
    // Using an external injected resource would be better, but it's really cumbersome here because
    // this class is created inside generators
    static int counter;

    private final EventBus eventBus;

    private State state;

    public NotifyingAsyncCallback(EventBus eventBus) {
        this.eventBus = eventBus;
        state = State.UNKNOWN;
    }

    @Override
    public final void onFailure(final Throwable caught) {
        assert state == State.INITIALIZED || state == State.LOADING;

        if (state == State.LOADING) {
            counter--;
            if (counter == 0) {
                AsyncCallFailEvent.fire(eventBus, caught);
            }
        }
        state = State.FAILED;
        failure(caught);
    }

    @Override
    public final void onSuccess(T result) {
        assert state == State.INITIALIZED || state == State.LOADING;
        if (state == State.LOADING) {
            counter--;
            if (counter == 0) {
                AsyncCallSucceedEvent.fire(eventBus);
            }
        }
        state = State.SUCCEEDED;
        success(result);
    }

    /**
     * This method is used to place that the async callback in its {@code INITIALIZED} stated. It
     * should be call prior to invoking any RPC that takes this callback as a parameter. See
     * {@link NotifyingAsyncCallback} for usage.
     */
    public void prepare() {
        assert state != State.LOADING;
        state = State.INITIALIZED;
    }

    /**
     * This method is used to indicate that the async callback is currently loading. It will not
     * do anything if the call is already finished (i.e. in the case of an immediate failure or
     * success). See {@link NotifyingAsyncCallback} for usage.
     */
    public void checkLoading() {
        assert state != State.UNKNOWN;
        if (state == State.INITIALIZED) {
            counter++;
            if (counter == 1) {
                AsyncCallStartEvent.fire(eventBus);
            }
            state = State.LOADING;
        }
    }

    /**
     * This method is called whenever the asynchronous call returns successfully.
     *
     * @param result the return value of the remote produced call.
     */
    protected abstract void success(T result);

    /**
     * This method is called whenever the asynchronous call returns with a failure. Override this
     * method if you want to perform clean-up operation when an async call fails. The default
     * implementation is empty.
     *
     * @param caught failure encountered while executing a remote procedure call.
     */
    protected void failure(Throwable caught) {
    }
}
