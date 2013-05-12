/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
