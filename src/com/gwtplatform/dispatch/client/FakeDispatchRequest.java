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

import com.google.gwt.http.client.Request;

public class FakeDispatchRequest implements DispatchRequest {

  private Request request;
  private boolean pending;
  private boolean cancelled;

  FakeDispatchRequest() {
    this.pending = true;
  }

  void setRequest(Request request) {
    this.request = request;
  }

  void setCompleted() {
    pending = false;
  }
  
  boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void cancel() {
    if (request != null) {
      request.cancel();
    } else {
      cancelled = true;
    }
  }

  @Override
  public boolean isPending() {
    if (request != null) {
      return request.isPending();
    } else {
      return pending && !cancelled;
    }
  }

}
