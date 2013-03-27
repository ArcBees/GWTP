package com.arcbees.carsample.client.application.cars.car;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.cars.car.navigation.NavigationTab;
import com.arcbees.carsample.client.application.cars.car.navigation.NavigationTabEvent;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.proxy.NotifyingAsyncCallback;
import com.gwtplatform.mvp.client.proxy.PlaceImpl;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyImpl;
import com.gwtplatform.mvp.client.proxy.ProxyPlaceImpl;

public class CarProxyImpl extends ProxyPlaceImpl<CarPresenter> implements CarPresenter.MyProxy {
    public static class WrappedProxy extends ProxyImpl<CarPresenter> {
        @Inject
        public WrappedProxy(Provider<CarPresenter> presenter) {
            this.presenter = new StandardProvider<CarPresenter>(presenter);
        }
    }

    private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

    @Inject
    public CarProxyImpl(@Assisted final CarPresenterProvider carPresenterProvider, @Assisted final String nameToken) {
        carPresenterProvider.setProxy(this);

        setProxy(new WrappedProxy(carPresenterProvider));
        setPlace(new PlaceImpl(nameToken));
    }

    @Override
    protected void bind(PlaceManager placeManager, final EventBus eventBus) {
        super.bind(placeManager, eventBus);

        NavigationTabEvent.NavigationTabHandler navigationTabHandler = new NavigationTabEvent.NavigationTabHandler() {
            @Override
            public void onCloseTab(NavigationTab element) {
                closeTab(element, eventBus);
            }

            @Override
            public void onRevealTab(NavigationTab element) {
            }
        };

        handlers.add(eventBus.addHandlerToSource(NavigationTabEvent.getType(), this, navigationTabHandler));
    }

    private void closeTab(final NavigationTab element, EventBus eventBus) {
        getPresenter(new NotifyingAsyncCallback<CarPresenter>(eventBus) {
            @Override
            protected void success(CarPresenter result) {
                if (element == result) {
                    unbind();
                }
            }
        });
    }

    private void unbind() {
        for (HandlerRegistration handler : handlers) {
            handler.removeHandler();
        }
    }
}
