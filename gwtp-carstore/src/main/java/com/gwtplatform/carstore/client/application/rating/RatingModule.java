package com.gwtplatform.carstore.client.application.rating;

import com.gwtplatform.carstore.client.application.rating.ui.EditRatingPresenter;
import com.gwtplatform.carstore.client.application.rating.ui.EditRatingView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class RatingModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(RatingPresenter.class, RatingPresenter.MyView.class, RatingView.class,
                RatingPresenter.MyProxy.class);

        bindPresenter(RatingDetailPresenter.class, RatingDetailPresenter.MyView.class, RatingDetailView.class,
                RatingDetailPresenter.MyProxy.class);

        bindSingletonPresenterWidget(EditRatingPresenter.class, EditRatingPresenter.MyView.class, EditRatingView.class);
    }
}
