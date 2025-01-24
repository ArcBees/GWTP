/*
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

/**
 * The interface for the {@link Proxy} of a {@link Presenter} that has
 * a name token and can be displayed within a
 * {@link com.gwtplatform.mvp.client.TabContainerPresenter TabContainerPresenter}'s main area.
 * If the presenter is not associated to a name token use {@link NonLeafTabContentProxy} instead.
 * <p>
 * Example of use:
 * <pre>
 * {@literal @}ProxyCodeSplit
 * {@literal @}NameToken("homepage")
 * {@literal @}TabInfo(container = MainPagePresenter.class, priority = 0, label = "Home")
 * public interface MyProxy extends TabContentProxyPlace&lt;HomePagePresenter&gt; { }
 * </pre>
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 * @see com.gwtplatform.mvp.client.annotations.TabInfo TabInfo
 */
public interface TabContentProxyPlace<P extends Presenter<?, ?>> extends
        TabContentProxy<P>, ProxyPlace<P> {
}
