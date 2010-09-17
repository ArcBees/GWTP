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
 * {@link ClientActionHandler}s. A {@link ClientDispatchRequest} will be return
 * by {@link DispatchAsync) to the code that requested the command be executed.
 * <p/> Initially it does not contain an http {@link Request} , but then when an
 * http {@link Request} is established, the request should be populated by
 * calling {@link #setRequest(Request)}.
 * 
 * @author Brendan Doherty
 */
public class ClientDispatchRequest implements DispatchRequest {

  private Request request;
  private boolean pending;
  private boolean cancelled;

  public ClientDispatchRequest() {
    this.pending = true;
  }

  /**
   * Populates the {@link ClientDispatchRequest} object with a {@link Request}.
   * </p> If the code that requested the command be executed chooses to cancel
   * the {@link DispatchRequest} and the {@link Request} that has been passed is
   * still pending, it will be cancelled.
   * 
   * @param request The {@link Request} object.
   */
  public void setRequest(Request request) {
    if (this.request != null) {
      throw new RuntimeException("Request can only be set once.");
    }
    this.request = request;
    if (this.cancelled) {
      this.request.cancel();
    }
  }

  /**
   * Causes subsequent calls to {@link #isPending()} return false;
   */
  void setCompleted() {
    pending = false;
  }

  /**
   * Checks to see if the code that requested the command be executed has chosen
   * to cancel this request.
   * 
   * @return Has the request has been cancelled.
   */
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
