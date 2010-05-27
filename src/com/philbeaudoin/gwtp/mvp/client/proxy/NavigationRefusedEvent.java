/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.philbeaudoin.gwtp.mvp.client.EventBus;

/**
 * Event fired when a user refuse to leave a page.
 * 
 * @author Christian Goudreau
 */
public final class NavigationRefusedEvent extends GwtEvent<NavigationRefusedhandler> {
  private static final Type<NavigationRefusedhandler> TYPE = new Type<NavigationRefusedhandler>();
  
  public static Type<NavigationRefusedhandler> getType() {
    return TYPE;
  }
  
  @Override
  protected void dispatch(NavigationRefusedhandler handler) {
    handler.onNavigationRefused(this);
  }

  public static void fire(final EventBus eventBus) {
    eventBus.fireEvent(new NavigationRefusedEvent());
  }
  
  @Override
  public Type<NavigationRefusedhandler> getAssociatedType() {
    return getType();
  }
}