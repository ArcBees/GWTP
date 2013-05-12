/**
 * Copyright 2013 ArcBees Inc.
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

import com.google.inject.Provider;
import com.gwtplatform.carstore.shared.dto.CarDto;

public class CarPresenterProvider implements Provider<CarPresenter> {
    private final CarPresenterFactory carPresenterFactory;

    private CarPresenter.MyProxy proxy;
    private CarPresenter carPresenter;
    private CarDto carDto;

    @Inject
    CarPresenterProvider(CarPresenterFactory carPresenterFactory) {
        this.carPresenterFactory = carPresenterFactory;
    }

    @Override
    public CarPresenter get() {
        assert proxy != null : "You must call setProxy first";

        if (carPresenter == null) {
            carPresenter = carPresenterFactory.create(proxy, carDto);
        }

        return carPresenter;
    }

    public void setProxy(CarPresenter.MyProxy proxy) {
        this.proxy = proxy;
    }

    public void setCar(CarDto carDto) {
        this.carDto = carDto;
    }
}
