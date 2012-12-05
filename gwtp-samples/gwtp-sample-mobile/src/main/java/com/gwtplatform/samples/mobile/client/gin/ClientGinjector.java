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

package com.gwtplatform.samples.mobile.client.gin;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.samples.mobile.client.application.ApplicationPresenter;
import com.gwtplatform.samples.mobile.client.application.breadcrumbs.BreadcrumbsPresenter;
import com.gwtplatform.samples.mobile.client.application.product.ProductPresenter;
import com.gwtplatform.samples.mobile.client.application.products.ProductsPresenter;

/**
 */
public interface ClientGinjector extends Ginjector {
  EventBus getEventBus();

  AsyncProvider<ApplicationPresenter> getHomePresenter();

  Provider<BreadcrumbsPresenter> getBreadcrumbsPresenter();

  PlaceManager getPlaceManager();

  AsyncProvider<ProductsPresenter> getProductListPresenter();

  AsyncProvider<ProductPresenter> getProductPresenter();
}
