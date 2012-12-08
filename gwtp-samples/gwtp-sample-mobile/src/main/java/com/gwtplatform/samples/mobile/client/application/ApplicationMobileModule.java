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
import com.gwtplatform.samples.mobile.client.application.breadcrumbs.BreadcrumbsMobileView;
import com.gwtplatform.samples.mobile.client.application.breadcrumbs.BreadcrumbsPresenter;
import com.gwtplatform.samples.mobile.client.application.product.ProductMobileView;
import com.gwtplatform.samples.mobile.client.application.product.ProductPresenter;
import com.gwtplatform.samples.mobile.client.application.products.ProductsMobileView;
import com.gwtplatform.samples.mobile.client.application.products.ProductsPresenter;

/**
 * @author Brandon Donnelson
 */
public class ApplicationMobileModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Application Presenters
    bindPresenter(BreadcrumbsPresenter.class, BreadcrumbsPresenter.MyView.class, BreadcrumbsMobileView.class,
        BreadcrumbsPresenter.MyProxy.class);
    bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationMobileView.class,
        ApplicationPresenter.MyProxy.class);
    bindPresenter(ProductsPresenter.class, ProductsPresenter.MyView.class, ProductsMobileView.class,
        ProductsPresenter.MyProxy.class);
    bindPresenter(ProductPresenter.class, ProductPresenter.MyView.class, ProductMobileView.class,
        ProductPresenter.MyProxy.class);
  }
}
