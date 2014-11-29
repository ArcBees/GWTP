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

import javax.inject.Inject;

import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter.MyView;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.cars.car.CarProxyFactory;
import com.gwtplatform.carstore.client.application.testutils.PresenterTestModule;
import com.gwtplatform.carstore.client.application.testutils.PresenterWidgetTestBase;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.shared.api.CarResource;
import com.gwtplatform.carstore.shared.api.CarsResource;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.rest.delegates.client.ResourceDelegate;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static com.gwtplatform.dispatch.rest.delegates.test.DelegateTestUtils.givenDelegate;

@RunWith(JukitoRunner.class)
public class CarsPresenterTest extends PresenterWidgetTestBase {
    public static class Module extends PresenterTestModule {
        @Override
        protected void configurePresenterTest() {
            forceMock(CarProxyFactory.class);
        }
    }

    @Inject
    CarsPresenter carsPresenter;
    @Inject
    MyView view;
    @Inject
    MyProxy proxy;
    @Inject
    CarProxyFactory carProxyFactory;
    @Inject
    ResourceDelegate<CarsResource> carsDelegate;

    @Test
    public void onEditCar(PlaceManager placeManager, ManufacturerDto manufacturerDto) {
        // Given
        CarDto carDto = mock(CarDto.class);
        given(carDto.getManufacturer()).willReturn(manufacturerDto);
        given(carProxyFactory.create(carDto, carDto.getManufacturer().getName() + carDto.getModel())).willReturn(proxy);
        given(proxy.getNameToken()).willReturn("token");

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("token").build();

        // When
        carsPresenter.onEdit(carDto);

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onCreate(PlaceManager placeManager) {
        // Given
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.NEW_CAR).build();

        // When
        carsPresenter.onCreate();

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onFetchData(ArrayList<CarDto> carDtos) {
        // Given
        givenDelegate(carsDelegate).useResource(CarsResource.class)
                .and().succeed().withResult(carDtos)
                .when().getCars(0, 1);

        // When
        carsPresenter.fetchData(0, 1);

        // Then
        verify(view).displayCars(0, carDtos);
    }

    @Test
    public void onFetchDataThreeCars(ArrayList<CarDto> carDtos) {
        // Given
        givenDelegate(carsDelegate).useResource(CarsResource.class)
                .and().succeed().withResult(carDtos).when().getCars(0, 3);

        // When
        carsPresenter.fetchData(0, 3);

        // Then
        verify(view).displayCars(0, carDtos);
    }

    @Test
    public void onDelete(HasData<CarDto> hasCarData, Range range, CarsResource carsResource, CarResource carResource) {
        // Given
        CarDto carDto = new CarDto();
        carDto.setId(3L);

        given(carsResource.car(carDto.getId())).willReturn(carResource);

        // We delete the car
        givenDelegate(carsDelegate).useResource(carsResource)
                .and().succeed()
                .when(carResource).delete();

        // We fetch the cars after delete
        givenDelegate(carsDelegate)
                .succeed().withResult(new ArrayList<>())
                .when().getCars();

        // And display is setup
        given(view.getCarDisplay()).willReturn(hasCarData);

        // And range is setup
        HasData<CarDto> display = view.getCarDisplay();
        given(display.getVisibleRange()).willReturn(range);

        // When
        carsPresenter.onDelete(carDto);

        // Then
        verify(view).setCarsCount(-1);
    }
}
