/*
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

package com.gwtplatform.carstore.client.application.cars;

import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenterFactory;
import com.gwtplatform.carstore.client.application.cars.car.CarProxyImplFactory;
import com.gwtplatform.carstore.client.application.cars.car.RootCarPresenter;
import com.gwtplatform.carstore.client.application.cars.car.RootCarView;
import com.gwtplatform.carstore.client.application.cars.car.navigation.NavigationTabPresenter;
import com.gwtplatform.carstore.client.application.cars.car.navigation.NavigationTabView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class CarsModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().build(CarPresenterFactory.class));
        install(new GinFactoryModuleBuilder().build(CarProxyImplFactory.class));

        bindPresenter(RootCarPresenter.class, RootCarPresenter.MyView.class, RootCarView.class,
                RootCarPresenter.MyProxy.class);

        bindSingletonPresenterWidget(NavigationTabPresenter.class, NavigationTabPresenter.MyView.class,
                NavigationTabView.class);
    }
}
