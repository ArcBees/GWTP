package com.arcbees.carsample.client.application;

import com.arcbees.carsample.client.application.event.ActionBarVisibilityEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent;
import com.arcbees.carsample.client.application.widget.header.HeaderPresenter;
import com.arcbees.carsample.client.application.widget.message.MessagesPresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy>
        implements ChangeActionBarEvent.ChangeActionBarHandler, ActionBarVisibilityEvent.ActionBarVisibilityHandler {
    public interface MyView extends View {
        void adjustActionBar(Boolean actionBarVisible);

        void adjustLayout(Boolean tabsVisible);
    }

    @ProxyStandard
    public interface MyProxy extends Proxy<ApplicationPresenter> {
    }

    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();

    public static final Object TYPE_SetMessagesContent = new Object();
    public static final Object TYPE_SetHeaderContent = new Object();

    private final HeaderPresenter headerPresenter;
    private final MessagesPresenter messagesPresenter;

    @Inject
    public ApplicationPresenter(EventBus eventBus,
            MyView view,
            MyProxy proxy,
            HeaderPresenter headerPresenter,
            MessagesPresenter messagesPresenter) {
        super(eventBus, view, proxy);

        this.headerPresenter = headerPresenter;
        this.messagesPresenter = messagesPresenter;
    }

    @Override
    public void onActionBarVisible(ActionBarVisibilityEvent event) {
        getView().adjustActionBar(event.isVisible());
    }

    @Override
    public void onChangeActionBar(ChangeActionBarEvent event) {
        getView().adjustLayout(event.getTabsVisible());
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }

    @Override
    protected void onBind() {
        setInSlot(TYPE_SetHeaderContent, headerPresenter);
        setInSlot(TYPE_SetMessagesContent, messagesPresenter);

        addRegisteredHandler(ChangeActionBarEvent.getType(), this);
        addRegisteredHandler(ActionBarVisibilityEvent.getType(), this);
    }
}
