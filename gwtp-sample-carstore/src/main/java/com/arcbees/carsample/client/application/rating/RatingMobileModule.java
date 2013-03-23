package com.arcbees.carsample.client.application.rating;

import com.arcbees.carsample.client.application.rating.renderer.RatingCellFactory;
import com.arcbees.carsample.client.application.rating.ui.EditRatingPresenter;
import com.arcbees.carsample.client.application.rating.ui.EditRatingUiHandlers;
import com.arcbees.carsample.client.application.rating.ui.EditRatingView;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
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
