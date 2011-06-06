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

package com.gwtplatform.samples.hplace.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.samples.hplace.client.presenter.BreadcrumbsPresenter;
import com.gwtplatform.samples.hplace.client.presenter.HomePresenter;
import com.gwtplatform.samples.hplace.client.presenter.ProductListPresenter;
import com.gwtplatform.samples.hplace.client.presenter.ProductPresenter;

/**
 * This example illustrates GWTP's support for hierarchical ginjectors. This is useful
 * when using configuration-dependent gin modules. For example, say you have three gin modules:
 * <ul>
 * <li>{@code MainModuleShared}</li>
 * <li>{@code MainModuleRelease}</li>
 * <li>{@code MainModuleDebug}</li>
 * </ul>
 * Then you want to switch between two ginjectors based on your configuration:
 * <pre>
 *   @GinModules({DispatchAsyncModule.class, ClientModuleShared.class, ClientModuleRelease.class})
 *   public interface ClientGinjectorRelease extends ClientGinjectorBase {}
 * </pre>
 * or
 * <pre>
 *   @GinModules({DispatchAsyncModule.class, ClientModuleShared.class, ClientModuleDebug.class})
 *   public interface ClientGinjectorDebug extends ClientGinjectorBase {}
 * </pre>
 * Since you don't want to cut-and-past all your {@code get} methods you place them in
 * {@code ClientGinjectorBase}, as is done here.
 *
 * @author Philippe Beaudoin
 */
public interface ClientGinjectorBase extends Ginjector {
  EventBus getEventBus();
  AsyncProvider<HomePresenter> getHomePresenter();
  Provider<BreadcrumbsPresenter> getBreadcrumbsPresenter();
  PlaceManager getPlaceManager();
  AsyncProvider<ProductListPresenter> getProductListPresenter();
  AsyncProvider<ProductPresenter> getProductPresenter();
}
