package com.arcbees.carsample.client.application.widget;

import com.arcbees.carsample.client.application.widget.header.HeaderMobileView;
import com.arcbees.carsample.client.application.widget.header.HeaderPresenter;
import com.arcbees.carsample.client.application.widget.header.HeaderUiHandlers;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WidgetMobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindSingletonPresenterWidget(HeaderPresenter.class, HeaderPresenter.MyView.class,
                HeaderMobileView.class);

        bind(HeaderUiHandlers.class).to(HeaderPresenter.class);
    }
}
