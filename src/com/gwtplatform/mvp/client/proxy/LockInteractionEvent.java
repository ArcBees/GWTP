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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;

import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.HasEventBus;
import com.gwtplatform.mvp.client.RootPresenter;

/**
 * This event is fired whenever interaction should be locked throughout the application,
 * usually because a navigation operation is taking place and interacting could cause
 * unexpected state switches.
 * <p />
 * This event is typically fired by the {@link PlaceManager} whenever a navigation operation
 * starts or stops. It is handled by the default {@link RootPresenter} implementation. 
 * Override {@link RootPresenter#lockInteraction(boolean)} to customize the behaviour.
 * 
 * @author Philippe Beaudoin
 */
public class LockInteractionEvent extends GwtEvent<LockInteractionHandler> {

  private static Type<LockInteractionHandler> TYPE;

  /**
   * Fires a {@link LockInteractionEvent} 
   * into a source that has access to an {@link EventBus}
   * specifying whether interaction should be locked or unlocked.
   * 
   * @param source The source that fires this event ({@link HasEventBus}).
   * @param lock {@code true} to lock interaction, {@code false} to unlock it.
   */
  public static void fire(HasEventBus source, boolean lock) {
    source.fireEvent(new LockInteractionEvent(lock));
  }

  public static Type<LockInteractionHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<LockInteractionHandler>();
    }
    return TYPE;
  }

  private final boolean lock;

  public LockInteractionEvent(boolean lock) {
    this.lock = lock;
  }

  @Override
  public Type<LockInteractionHandler> getAssociatedType() {
    return getType();
  }

  public boolean shouldLock() {
    return lock;
  }

  @Override
  protected void dispatch(LockInteractionHandler handler) {
    handler.onLockInteraction(this);
  }

}
