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
 * A useful mixing class to define a {@link TabContentProxy} that is also a
 * {@link Place}.
 *
 * @param <T> The Presenter's type.
 *
 * @author Philippe Beaudoin
 */
public class TabContentProxyPlaceImpl<T extends Presenter<?, ?>> extends
    ProxyPlaceAbstract<T, TabContentProxy<T>> implements
    TabContentProxyPlace<T> {

  public TabContentProxyPlaceImpl() {
  }

  @Override
  public String getTargetHistoryToken() {
    return getNameToken();
  }

  @Override
  public TabData getTabData() {
    return proxy.getTabData();
  }

  @Override
  public Tab getTab() {
    return proxy.getTab();
  }

}
