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
 * Event fired when a user refuses to leave a page. See
 * {@link PlaceManager#setOnLeaveConfirmation}.
 *
 * @see NavigationEvent
 *
 * @author Christian Goudreau
 */
public final class NavigationRefusedEvent extends
    GwtEvent<NavigationRefusedHandler> {
  private static final Type<NavigationRefusedHandler> TYPE = new Type<NavigationRefusedHandler>();

  /**
   * Fires a {@link NavigationRefusedEvent}
   * into a source that has access to an {@link com.google.web.bindery.event.shared.EventBus}.
   *
   * @param source The source that fires this event ({@link HasHandlers}).
   */
  public static void fire(final HasHandlers source) {
    source.fireEvent(new NavigationRefusedEvent());
  }

  public static Type<NavigationRefusedHandler> getType() {
    return TYPE;
  }

  @Override
  public Type<NavigationRefusedHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(NavigationRefusedHandler handler) {
    handler.onNavigationRefused(this);
  }
}