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

package com.gwtplatform.samples.tab.client.presenter;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabContainerPresenter;
import com.gwtplatform.mvp.client.TabPanel;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * The base class for {@link HomePresenter}. Used mainly to test issue 222:
 * http://code.google.com/p/gwt-platform/issues/detail?id=222.
 *
 * @author Philippe Beaudoin
 *
 * @param <V> The specific type of the {@link View}. Must implement {@link TabPanel}.
 * @param <P> The specific type of the {@link Proxy}.
 */
public abstract class HomePresenterBase<V extends View & TabPanel, P extends Proxy<?>>
    extends TabContainerPresenter<V, P> {

  public HomePresenterBase(EventBus eventBus, V view, P proxy,
      Object tabContentSlot, Type<RequestTabsHandler> requestTabsEventType) {
    super(eventBus, view, proxy, tabContentSlot, requestTabsEventType);
  }

  /**
   * Use this in leaf presenters, inside their {@link #revealInParent} method.
   */
  @ContentSlot
  public static final Type<RevealContentHandler<?>> TYPE_SetTabContent = new Type<RevealContentHandler<?>>();
}
