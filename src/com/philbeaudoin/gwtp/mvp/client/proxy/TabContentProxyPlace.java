package com.philbeaudoin.gwtp.mvp.client.proxy;

/**
 * Copyright 2010 Philippe Beaudoin
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


import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.Tab;

/**
 * A useful mixing class to define a {@link TabContentProxy} that is also
 * a {@link Place}.
 * 
 * @author Philippe Beaudoin
 */
public class TabContentProxyPlace<P extends Presenter> 
extends ProxyPlaceAbstract<P, TabContentProxy<P>>
implements TabContentProxy<P> {

  public TabContentProxyPlace() {
  }
  
  @Override
  public float getPriority() {
    return proxy.getPriority();
  }

  @Override
  public Tab getTab() {
    return proxy.getTab();
  }

  @Override
  public String getLabel() {
    return proxy.getLabel();
  }

  @Override
  public String getHistoryToken() {
    return getNameToken();
  }

}
