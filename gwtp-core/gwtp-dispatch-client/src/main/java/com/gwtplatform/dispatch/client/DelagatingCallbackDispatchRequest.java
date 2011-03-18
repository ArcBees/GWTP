/**
 * Copyright 2010 ArcBees Inc.
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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An implementation of {@link DispatchRequest} that should be used by
 * {@link com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler ClientActionHandler}s
 * that make asynchronous calls that return a {@link com.google.gwt.http.client.Request Request}.
 *
 * This class also takes a {@link DispatchRequest} and delegate work to this {@link DispatchRequest}.
 *
 * This class is used within
 * {@link com.gwtplatform.dispatch.client.actionhandler.caching.AbstractCachingClientActionHandler AbstractCachingClientActionHandler}
 * to be able to store inside an HashMap {@link DefaultCallbackDispatchRequest} while keeping
 * {@link GwtHttpDispatchRequest} nature.
 *
 * @param <R> The type of the {@link AsyncCallback}.
 *
 * @author Christian Goudreau
 */
public class DelagatingCallbackDispatchRequest<R> implements CallbackDispatchRequest<R> {
  private final DispatchRequest request;
  private final AsyncCallback<R> callback;

  public DelagatingCallbackDispatchRequest(DispatchRequest request,
      AsyncCallback<R> callback) {

    this.request = request;
    this.callback = callback;
  }

  @Override
  public void cancel() {
    request.cancel();
  }

  @Override
  public boolean isPending() {
    return request.isPending();
  }

  @Override
  public void onFailure(Throwable caught) {
    callback.onFailure(caught);
  }

  @Override
  public void onSuccess(R result) {
    callback.onSuccess(result);
  }
}
