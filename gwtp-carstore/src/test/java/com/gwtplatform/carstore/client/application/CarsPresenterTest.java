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

package com.gwtplatform.carstore.client.application;

import java.util.ArrayList;
import java.util.List;

import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter;
import com.gwtplatform.carstore.client.application.cars.car.CarProxyFactory;
import com.gwtplatform.carstore.client.application.testutils.CarsServiceImpl;
import com.gwtplatform.carstore.client.application.testutils.PresenterTestModule;
import com.gwtplatform.carstore.client.application.testutils.PresenterWidgetTestBase;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.CarsService;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
public class CarsPresenterTest extends PresenterWidgetTestBase {
    public static class Module extends PresenterTestModule {
        @Override
        protected void configurePresenterTest() {
            forceMock(CarProxyFactory.class);
            bind(CarsService.class).to(CarsServiceImpl.class).in(TestSingleton.class);
        }
    }

    @Inject
    CarsPresenter carsPresenter;
    @Inject
    CarsPresenter.MyView view;
    @Inject
    CarProxyFactory carProxyFactory;
    @Inject
    CarPresenter.MyProxy proxy;

    @Test
    public void onEditCar(PlaceManager placeManager, ManufacturerDto manufacturerDto) {
        // Given
        CarDto carDto = mock(CarDto.class);
        when(carDto.getManufacturer()).thenReturn(manufacturerDto);
        when(carProxyFactory.create(carDto, carDto.getManufacturer().getName() + carDto.getModel())).thenReturn(proxy);
        when(proxy.getNameToken()).thenReturn("token");

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("token").build();

        // When
        carsPresenter.onEdit(carDto);

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onCreate(PlaceManager placeManager) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.newCar).build();

        // When
        carsPresenter.onCreate();

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onDelete(CarDto carDto, HasData<CarDto> hasCarData, Range range) {
        // Given we have DeleteCarAction
        dispatcher.given(new TypeLiteral<RestAction<Void>>() {}).willReturn(null);

        // And GetCarAction for fetching after delete
        List<CarDto> result = new ArrayList<CarDto>();
        dispatcher.given(new TypeLiteral<RestAction<List<CarDto>>>() {}).willReturn(result);

        // And display is setup
        when(view.getCarDisplay()).thenReturn(hasCarData);

        // And range is setup
        HasData<CarDto> display = view.getCarDisplay();
        when(display.getVisibleRange()).thenReturn(range);

        // When
        carsPresenter.onDelete(carDto);

        // Then
        verify(view).setCarsCount(-1);
    }

    @Test
    public void onFetchData(ArrayList<CarDto> carDtos) {
        // Given
        List<CarDto> result = new ArrayList<CarDto>();
        dispatcher.given(new TypeLiteral<RestAction<List<CarDto>>>() {}).willReturn(result);

        // When
        carsPresenter.fetchData(0, 1);

        // Then
        verify(view).displayCars(0, carDtos);
    }

    @Test
    public void onFetchDataThreeCars(ArrayList<CarDto> carDtos) {
        // Given
        dispatcher.given(new TypeLiteral<RestAction<List<CarDto>>>() {}).willReturn(carDtos);

        // When
        carsPresenter.fetchData(0, 3);

        // Then
        verify(view).displayCars(0, carDtos);
    }
}
