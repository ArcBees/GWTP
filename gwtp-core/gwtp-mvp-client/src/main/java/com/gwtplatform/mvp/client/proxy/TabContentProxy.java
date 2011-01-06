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

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.Tab;

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
   * Retrieves the history token to show when this tab is displayed. In the
   * fairly typical scenario where a tab directly contains a {@link ProxyPlace},
   * this should return the name token of the proxy place. In the case of tabs
   * that contain other tab presenters, this should return the name token of a
   * leaf-level proxy.
   * 
   * @return The default history token to show.
   */
  String getHistoryToken();

  /**
   * Retrieves the text label to show on that tab.
   * 
   * @return The text label.
   */
  String getLabel();

  /**
   * A tab priority indicates where it should appear within the tab strip. A tab
   * with low priority will be placed more towards the left of the strip. Two
   * tabs with the same priority will be placed in an arbitrary order.
   * 
   * @return The priority.
   */
  float getPriority();

  /**
   * Retrieves the tab object associated with this presenter.
   * 
   * @return The tab object.
   */
  Tab getTab();

}
