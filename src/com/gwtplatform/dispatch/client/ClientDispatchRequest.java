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

/**
 * An implementation of {@link DispatchRequest} that is used with
 * {@link ClientActionHandler}s. Initially it does not contain an http
 * {@link com.google.gwt.http.client.Request Request}, but then when a
 * {@link com.google.gwt.http.client.Request Request} is established, the
 * request can be populated by calling {@link #setRequest(Request)}.
 * 
 * @author Brendan Doherty
 */
public class ClientDispatchRequest implements DispatchRequest {

  private Request request;
  private boolean pending;
  private boolean cancelled;

  ClientDispatchRequest() {
    this.pending = true;
  }

  public void setRequest(Request request) {
    if (this.request != null) {
      throw new RuntimeException("Request can only be set once.");
    }
    this.request = request;
    if (this.cancelled) {
      this.request.cancel();
    }
  }

  void setCompleted() {
    pending = false;
  }

  public boolean isCancelled() {
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
