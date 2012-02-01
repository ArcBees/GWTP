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

package com.gwtplatform.mvp.client;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.mvp.client.proxy.TabContentProxy;

/**
 * This event is fired whenever a tab contained in a {@link TabContainerPresenter} wants to change
 * its information.
 *
 * @author Philippe Beaudoin
 */
public final class ChangeTabEvent extends GwtEvent<ChangeTabHandler> {

  /**
   * Fires a {@link ChangeTabEvent} with a specific
   * {@link com.google.gwt.event.shared.GwtEvent.Type} into a source that has access to an
   * {@link com.google.web.bindery.event.shared.EventBus}.
   *
   * @param source The source that fires this event ({@link HasHandlers}).
   * @param type The specific event {@link com.google.gwt.event.shared.GwtEvent.Type}.
   * @param tabContentProxy The {@link TabContentProxy} for this tab, with modified information.
   */
  public static void fire(final HasHandlers source,
      final Type<ChangeTabHandler> type, TabContentProxy<?> tabContentProxy) {

    source.fireEvent(new ChangeTabEvent(type, tabContentProxy));
  }

  private final TabContentProxy<?> tabContentProxy;
  private final Type<ChangeTabHandler> type;

  /**
   * Creates an event for requesting the modification to a tab displayed in a
   * {@link TabContainerPresenter}.
   *
   * @param type The specific {@link com.google.gwt.event.shared.GwtEvent.Type} of this event.
   * @param tabContentProxy The {@link TabContentProxy} for this tab, with modified information.
   */
  public ChangeTabEvent(final Type<ChangeTabHandler> type,
      TabContentProxy<?> tabContentProxy) {
    this.type = type;
    this.tabContentProxy = tabContentProxy;
  }

  @Override
  public Type<ChangeTabHandler> getAssociatedType() {
    return type;
  }

  public TabContentProxy<?> getTabContentProxy() {
    return tabContentProxy;
  }

  @Override
  protected void dispatch(ChangeTabHandler handler) {
    handler.onChangeTab(this);
  }

}
