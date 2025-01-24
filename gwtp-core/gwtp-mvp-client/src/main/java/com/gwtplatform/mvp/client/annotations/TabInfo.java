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

package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.gwtplatform.mvp.client.TabContainerPresenter;

/**
 * Annotation used to specify various information on a tab. The {@link #container}
 * parameter is mandatory. This annotation can be used in one of three ways:
 * <p>
 * <b>1) Annotating the proxy</b><br />
 * You can directly annotate your proxy with it, in which case the {@link #label}
 * parameter must be specified. For example:
 * <pre>
 * {@code @}ProxyCodeSplit
 * {@code @}NameToken("HOME")
 * {@code @}TabInfo(
 *    container = TabStripPresenter.class,
 *    priority = 0,
 *    label = "Home Page" )
 * public interface MyProxy extends TabContentProxyPlace&lt;HomeTabPresenter&gt; { }</pre>
 * If this presenter is not a place then you also need to specify the {@link #nameToken}
 * parameter. This is useful for setting up nested tab panels, for example:
 * <pre>
 * {@code @}ProxyCodeSplit
 * {@code @}TabInfo(
 *    container = TabStripPresenter.class,
 *    priority = 1,
 *    nameToken = "SETTINGS-USER")
 * public interface MyProxy extends TabContentProxy&lt;SettingsTabStripPresenter&gt; { }
 * </pre>
 * <p>
 * <b>2) Annotating a static method returning {@code String}</b><br />
 * You can annotate a static method returning a {@code String}, in which case you must
 * not specify the {@link #label}. Again, specify the {@link #nameToken} parameter only
 * if your presenter is not a place.
 * <p>
 * The method you annotate can optionally accept exactly 1 parameter having the type
 * of your custom {@link com.google.gwt.inject.client.Ginjector}. Here's an example of
 * this usage:
 * <pre>
 * {@code @}ProxyCodeSplit
 * {@code @}NameToken("HOME")
 * public interface MyProxy extends TabContentProxyPlace&lt;HomeTabPresenter&gt; { }
 *
 * {@code @}TabInfo(
 *    container = TabStripPresenter.class,
 *    priority = 0 )
 * static String getLabel(MyGingector ginjector) {
 *   return gingector.getTranslations().homePageLabel();
 * }
 * </pre>
 * <p>
 * <b>3) Annotating a static method returning
 * {@code TabData}</b><br />
 * You can annotate a static method returning a {@link com.gwtplatform.mvp.client.TabData},
 * in which case you must not specify either the {@link #label} nor the {@link #priority}.
 * Again, specify the {@link #nameToken} parameter only if your presenter is not a place.
 * <p>
 * The method you annotate can optionally accept parameters having either the type
 * of your custom {@link com.google.gwt.inject.client.Ginjector} or any type that is provided by
 * the ginjector. Here are two examples of this usage:
 * <p>
 * <pre>
 * {@code @}ProxyCodeSplit
 * public interface MyProxy extends TabContentProxy&lt;SettingsTabStripPresenter&gt; { }
 *
 * {@code @}TabInfo(
 *    container = TabStripPresenter.class,
 *    priority = 1,
 *    nameToken = "SETTINGS-USER")
 * static TabData getTabData(MyGingector ginjector) {
 *   return ginjector.getTabDataFactory().createUserTabData();
 * }
 * </pre>
 * <p>
 * <pre>
 * {@code @}TabInfo(container = ApplicationPresenter.class)
 * static TabData getTabData(Messages msgs, IsAdminGateKeeper keeper) {
 *   return new TabDataExt(msgs.adminTitle(), 1000, keeper);;
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TabInfo {

    /**
     * This parameter indicates the presenter into which this tab will be
     * displayed. Typically, the container will contain a tab strip and
     * this tab will be displayed in it.
     *
     * @return The container presenter.
     */
    Class<? extends TabContainerPresenter<?, ?>> container();

    /**
     * A static string corresponding to the label to display on the tab.
     * For more flexibility, see other ways to use the {@link TabInfo}
     * annotation.
     *
     * @return The label.
     */
    String label() default "";

    /**
     * The priority is a <b>non-negative</b> integer that controls the order
     * in which tabs are displayed. For more information see
     * {@link com.gwtplatform.mvp.client.TabData#getPriority()}.
     *
     * @return The priority, or a negative integer if not set.
     */
    int priority() default -1;

    /**
     * The name token indicates which place should be visited when you click
     * on this tab. You should only specify this if your presenter is not
     * a place. For example, if it is a tab panel meant to be contained in
     * another tab panel.
     *
     * @return The name token.
     */
    String nameToken() default "";
}
