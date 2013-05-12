/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.rating;

import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.gwtplatform.carstore.client.application.rating.renderer.RatingCellFactory;
import com.gwtplatform.carstore.client.application.rating.ui.EditRatingPresenter;
import com.gwtplatform.carstore.client.application.rating.ui.EditRatingUiHandlers;
import com.gwtplatform.carstore.client.application.rating.ui.EditRatingView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class RatingMobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().build(RatingCellFactory.class));

        bindPresenter(RatingPresenter.class, RatingPresenter.MyView.class, RatingMobileView.class,
                RatingPresenter.MyProxy.class);

        bindPresenter(RatingDetailPresenter.class, RatingDetailPresenter.MyView.class, RatingDetailView.class,
                RatingDetailPresenter.MyProxy.class);

        bindSingletonPresenterWidget(EditRatingPresenter.class, EditRatingPresenter.MyView.class, EditRatingView.class);

        bind(EditRatingUiHandlers.class).to(EditRatingPresenter.class);
        bind(RatingUiHandlers.class).to(RatingPresenter.class);
        bind(RatingDetailUiHandlers.class).to(RatingDetailPresenter.class);
    }
}
