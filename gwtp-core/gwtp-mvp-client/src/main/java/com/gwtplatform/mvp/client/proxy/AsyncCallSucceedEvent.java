/**
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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Event fired after any asynchronous call to the server performed by GWTP MVP has succeeded.
 * Such asynchronous calls only occur when using code splitting.
 *
 * @see AsyncCallSucceedHandler
 * @see AsyncCallStartEvent
 * @see AsyncCallFailEvent
 *
 * @author Philippe Beaudoin
 */
public class AsyncCallSucceedEvent extends GwtEvent<AsyncCallSucceedHandler> {
  private static final Type<AsyncCallSucceedHandler> TYPE = new Type<AsyncCallSucceedHandler>();

  /**
   * Fires a {@link AsyncCallSucceedEvent}
   * into a source that has access to an {@link com.google.gwt.event.shared.EventBus}.
   *
   * @param source The source that fires this event ({@link HasHandlers}).
   */
  public static void fire(final HasHandlers source) {
    source.fireEvent(new AsyncCallSucceedEvent());
  }

  AsyncCallSucceedEvent() {
  }

  public static Type<AsyncCallSucceedHandler> getType() {
    return TYPE;
  }

  @Override
  public Type<AsyncCallSucceedHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(AsyncCallSucceedHandler handler) {
    handler.onAsyncCallSucceed(this);
  }
}
