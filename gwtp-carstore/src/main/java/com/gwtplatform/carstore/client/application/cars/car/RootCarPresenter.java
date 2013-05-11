package com.gwtplatform.carstore.client.application.cars.car;

import javax.inject.Inject;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.cars.car.navigation.NavigationTabPresenter;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class RootCarPresenter extends Presenter<RootCarPresenter.MyView, RootCarPresenter.MyProxy> {
    public interface MyView extends View {
    }

    @ProxyStandard
    public interface MyProxy extends Proxy<RootCarPresenter> {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_SetCarContent = new GwtEvent
            .Type<RevealContentHandler<?>>();

    public static final Object SLOT_TAB_BAR = new Object();

    private final NavigationTabPresenter navigationTabPresenter;

    @Inject
    RootCarPresenter(EventBus eventBus,
                     MyView view,
                     MyProxy proxy,
                     NavigationTabPresenter navigationTabPresenter) {
        super(eventBus, view, proxy);

        this.navigationTabPresenter = navigationTabPresenter;
    }

    @Override
    protected void onBind() {
        super.onBind();

        setInSlot(SLOT_TAB_BAR, navigationTabPresenter);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.SLOT_MAIN_CONTENT, this);
    }
}
