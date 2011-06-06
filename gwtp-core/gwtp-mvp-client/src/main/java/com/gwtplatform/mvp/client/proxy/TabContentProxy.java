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

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;

/**
 * The interface for the {@link Proxy} of a {@link Presenter} that can
 * be displayed within a
 * {@link com.gwtplatform.mvp.client.TabContainerPresenter TabContainerPresenter}'s main area.
 * If the presenter is associated to a name token use {@link TabContentProxyPlace} instead.
 * Example of use:
 * <pre>
 *{@literal @}ProxyCodeSplit
 *{@literal @}TabInfo(container = MainPagePresenter.class, priority = 0,
 *          label = "Home", nameToken = "homepage")
 * public interface MyProxy extends TabContentProxy&lt;ThisPresenter&gt; { }
 * </pre>
 * In this case, the {@code nameToken} parameter indicates the presenter to reveal
 * when this tab is selected.
 *
 * @see com.gwtplatform.mvp.client.annotations.TabInfo TabInfo
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 *
 * @author Philippe Beaudoin
 */
public interface TabContentProxy<P extends Presenter<?, ?>> extends Proxy<P> {

  /**
   * Gets the history token that should be accessed when the tab is clicked.
   * In the fairly typical scenario where a tab directly contains a place,
   * this should return the name token of that place. In the case of tabs
   * that contain non-leaf presenters (for example, other tabs), this should
   * return the name token of a leaf-level presenter.
   *
   * @return The history token.
   */
  String getTargetHistoryToken();

  /**
   * Retrieves the {@link TabData} that should be used to create this tab.
   *
   * @return The tab data.
   */
  TabData getTabData();

  /**
   * Retrieves the {@link Tab} object that was created from the
   * {@link TabData} returned by {@link #getTabData()}.
   *
   * @return The tab.
   */
  Tab getTab();

}
