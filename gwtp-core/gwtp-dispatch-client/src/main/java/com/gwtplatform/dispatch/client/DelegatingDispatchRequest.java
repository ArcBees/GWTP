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

import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * An implementation of {@link DispatchRequest} that is used in conjunction with
 * {@link com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler ClientActionHandler}s.
 * <p/>
 * As the ClientActionHandler may be provided asynchronously, the
 * {@link ClientActionHandlerDispatchRequest} will initially not contain an {@link DispatchRequest}.
 * Once the {@link com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler ClientActionHandler} is
 * executed, this be populated with a {@link DelegatingDispatchRequest} by
 * calling {@link #setDelegate(DispatchRequest)}.
 *
 * @author Brendan Doherty
 */
public class DelegatingDispatchRequest implements DispatchRequest {

  private boolean cancelled;
  private DispatchRequest delegate;

  public DelegatingDispatchRequest() {
  }

  /**
   * Populates the {@link DelegatingDispatchRequest} object with a
   * {@link DispatchRequest}. <p/> If the code that requested the command to be
   * executed has already chosen to cancel the {@link DispatchRequest}, the
   * {@link DispatchRequest} that has been passed will be immediately cancelled.
   *
   * @param delegate The {@link DispatchRequest} object.
   */
  public void setDelegate(DispatchRequest delegate) {
    if (cancelled) {
      if (delegate != null) {
        this.delegate.cancel();
      }
    } else {
      if (delegate == null) {
        throw new NullPointerException("delegate");
      }
      if (this.delegate != null) {
        throw new RuntimeException("Delegate can only be set once.");
      }
      this.delegate = delegate;
    }
  }

  @Override
  public void cancel() {
    if (delegate != null) {
      delegate.cancel();
    } else {
      cancelled = true;
    }
  }

  @Override
  public boolean isPending() {
    if (delegate != null) {
      return delegate.isPending();
    } else {
      return !cancelled;
    }
  }
}
