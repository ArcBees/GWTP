package com.arcbees.carsample.client.application.widget.message;

import com.arcbees.carsample.client.application.widget.message.ui.MessageWidgetFactory;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MessagesModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().build(MessageWidgetFactory.class));

        bindSingletonPresenterWidget(MessagesPresenter.class, MessagesPresenter.MyView.class, MessagesView.class);
    }
}
