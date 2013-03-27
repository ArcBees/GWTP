package com.arcbees.carsample.client.application.widget.message;

import com.arcbees.carsample.client.application.event.DisplayMessageEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class MessagesPresenter extends PresenterWidget<MessagesPresenter.MyView>
        implements DisplayMessageEvent.DisplayMessageHandler {
    public interface MyView extends View {
        void addMessage(Message message);
    }

    @Inject
    public MessagesPresenter(final EventBus eventBus, final MyView view) {
        super(eventBus, view);
    }

    @Override
    public void onDisplayMessage(DisplayMessageEvent event) {
        getView().addMessage(event.getMessage());
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(DisplayMessageEvent.getType(), this);
    }
}
