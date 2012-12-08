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

package com.gwtplatform.samples.mobile.client.application;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.samples.mobile.client.application.breadcrumbs.BreadcrumbsPresenter;
import com.gwtplatform.samples.mobile.client.application.breadcrumbs.BreadcrumbsTabletView;
import com.gwtplatform.samples.mobile.client.application.product.ProductPresenter;
import com.gwtplatform.samples.mobile.client.application.product.ProductTabletView;
import com.gwtplatform.samples.mobile.client.application.products.ProductsPresenter;
import com.gwtplatform.samples.mobile.client.application.products.ProductsTabletView;

/**
 * @author Brandon Donnelson
 */
public class ApplicationTabletModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Application Presenters
    bindPresenter(BreadcrumbsPresenter.class, BreadcrumbsPresenter.MyView.class, BreadcrumbsTabletView.class,
        BreadcrumbsPresenter.MyProxy.class);
    bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationTabletView.class,
        ApplicationPresenter.MyProxy.class);
    bindPresenter(ProductsPresenter.class, ProductsPresenter.MyView.class, ProductsTabletView.class,
        ProductsPresenter.MyProxy.class);
    bindPresenter(ProductPresenter.class, ProductPresenter.MyView.class, ProductTabletView.class,
        ProductPresenter.MyProxy.class);
  }
}
