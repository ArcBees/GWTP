package com.gwtplatform.carstore.client.application.widget;

import com.gwtplatform.carstore.client.application.widget.header.HeaderPresenter;
import com.gwtplatform.carstore.client.application.widget.header.HeaderUiHandlers;
import com.gwtplatform.carstore.client.application.widget.header.HeaderView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WidgetModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindSingletonPresenterWidget(HeaderPresenter.class, HeaderPresenter.MyView.class,
                HeaderView.class);

        bind(HeaderUiHandlers.class).to(HeaderPresenter.class);
    }
}
