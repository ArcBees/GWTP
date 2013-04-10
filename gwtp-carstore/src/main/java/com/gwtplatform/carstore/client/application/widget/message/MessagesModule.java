package com.gwtplatform.carstore.client.application.widget.message;

import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.gwtplatform.carstore.client.application.widget.message.ui.MessageWidgetFactory;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MessagesModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().build(MessageWidgetFactory.class));

        bindSingletonPresenterWidget(MessagesPresenter.class, MessagesPresenter.MyView.class, MessagesView.class);
    }
}
