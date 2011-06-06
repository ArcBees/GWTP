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

/**
 * This event is fired by the {@link TabContainerPresenter} to identify all
 * presenters that should display their tabs within it.
 *
 * @author Philippe Beaudoin
 */
public final class RequestTabsEvent extends GwtEvent<RequestTabsHandler> {

  /**
   * Fires a {@link RequestTabsEvent} with a specific {@link com.google.gwt.event.shared.GwtEvent.Type}
   * into a source that has access to an {@link com.google.gwt.event.shared.EventBus}.
   *
   * @param source The source that fires this event ({@link HasHandlers}).
   * @param type The specific event {@link com.google.gwt.event.shared.GwtEvent.Type}.
   * @param tabContainer The {@link TabContainerPresenter} requesting the tabs.
   */
  public static void fire(final HasHandlers source,
      final Type<RequestTabsHandler> type, TabContainerPresenter<?, ?> tabContainer) {

    source.fireEvent(new RequestTabsEvent(type, tabContainer));
  }

  private final TabContainerPresenter<?, ?> tabContainer;
  private final Type<RequestTabsHandler> type;

  /**
   * Creates an event for requesting the tabs that should be displayed in a
   * {@link TabContainerPresenter}.
   *
   * @param type The specific {@link com.google.gwt.event.shared.GwtEvent.Type} of this event.
   * @param tabContainer The {@link TabContainerPresenter} making the request.
   */
  public RequestTabsEvent(final Type<RequestTabsHandler> type,
      TabContainerPresenter<?, ?> tabContainer) {
    this.type = type;
    this.tabContainer = tabContainer;
  }

  @Override
  public Type<RequestTabsHandler> getAssociatedType() {
    return type;
  }

  public TabContainerPresenter<?, ?> getTabContainer() {
    return tabContainer;
  }

  @Override
  protected void dispatch(RequestTabsHandler handler) {
    handler.onRequestTabs(this);
  }

}
