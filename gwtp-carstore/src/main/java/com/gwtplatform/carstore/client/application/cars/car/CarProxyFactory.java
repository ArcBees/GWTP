package com.gwtplatform.carstore.client.application.cars.car;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class CarProxyFactory {
    private final Provider<CarPresenterProvider> carPresenterProvider;
    private final PlaceManager placeManager;
    private final EventBus eventBus;
    private final CarProxyImplFactory carProxyImplFactory;

    @Inject
    CarProxyFactory(Provider<CarPresenterProvider> carPresenterProvider,
                    PlaceManager placeManager,
                    EventBus eventBus,
                    CarProxyImplFactory carProxyImplFactory) {
        this.carPresenterProvider = carPresenterProvider;
        this.placeManager = placeManager;
        this.eventBus = eventBus;
        this.carProxyImplFactory = carProxyImplFactory;
    }

    public CarPresenter.MyProxy create(CarDto carDto, String nameToken) {
        CarPresenterProvider carPresenter = carPresenterProvider.get();
        carPresenter.setCar(carDto);

        CarProxyImpl carProxy = carProxyImplFactory.create(carPresenter, nameToken);
        carProxy.bind(placeManager, eventBus);

        return carProxy;
    }
}
