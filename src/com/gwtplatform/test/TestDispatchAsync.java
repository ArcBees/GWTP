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

package com.gwtplatform.test;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.gwtplatform.dispatch.client.DispatchAsync;
import com.gwtplatform.dispatch.client.DispatchRequest;
import com.gwtplatform.dispatch.client.DispatchService;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * This class is an implementation of {@link DispatchAsync} for use with test
 * cases that configure guice using a {@link MockHandlerModule}.
 * 
 * @author Brendan Doherty
 */

class TestDispatchAsync implements DispatchAsync {

  private DispatchService service;

  class TestingDispatchRequest implements DispatchRequest {

    public TestingDispatchRequest() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isPending() {
      return false;
    }
  }

  @Inject
  public TestDispatchAsync(TestDispatchService service) {
    this.service = service;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends Action<R>, R extends Result> DispatchRequest execute(
      A action, AsyncCallback<R> callback) {
    assert callback != null;
    R result = null;
    boolean fail = false;
    try {
      result = (R) service.execute("", action);
    } catch (Throwable caught) {
      fail = true;
      callback.onFailure(caught);
    }
    if (!fail) {
      callback.onSuccess(result);
    }
    return new TestingDispatchRequest();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends Action<R>, R extends Result> DispatchRequest undo(A action,
      R result, AsyncCallback<Void> callback) {
    boolean fail = false;
    try {
      service.undo("", (Action<Result>) action, result);
    } catch (Throwable caught) {
      fail = true;
      callback.onFailure(caught);
    }
    if (!fail) {
      callback.onSuccess(null);
    }
    return new TestingDispatchRequest();
  }

}
