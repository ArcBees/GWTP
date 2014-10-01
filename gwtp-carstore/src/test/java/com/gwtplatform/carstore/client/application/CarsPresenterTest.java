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
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.gwtplatform.dispatch.rest.client.ResourceDelegate;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    ResourceDelegate<CarsResource> carsResourceDelegate;
    @Inject
    CarsResource carsResource;
    @Inject
    CarResource carResource;

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
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.NEW_CAR).build();

        // When
        carsPresenter.onCreate();

        // Then
        verify(placeManager).revealPlace(eq(placeRequest));
    }

    @Test
    public void onFetchData(ArrayList<CarDto> carDtos) {
        // Given
        ArgumentCaptor<AsyncCallback> callback = captureResourceDelegateCallback(carsResourceDelegate, carsResource);
        callSuccessWith(callback, carDtos).when(carsResource).getCars(0, 1);

        // When
        carsPresenter.fetchData(0, 1);

        // Then
        verify(view).displayCars(0, carDtos);
    }

    @Test
    public void onFetchDataThreeCars(ArrayList<CarDto> carDtos) {
        // Given
        ArgumentCaptor<AsyncCallback> callback = captureResourceDelegateCallback(carsResourceDelegate, carsResource);
        callSuccessWith(callback, carDtos).when(carsResource).getCars(0, 3);

        // When
        carsPresenter.fetchData(0, 3);

        // Then
        verify(view).displayCars(0, carDtos);
    }

    @Test
    public void onDelete(CarDto carDto, HasData<CarDto> hasCarData, Range range) {
        // Given
        carDto.setId(3L);

        ArgumentCaptor<AsyncCallback> callback = captureResourceDelegateCallback(carsResourceDelegate, carsResource);

        // Given we delete the car
        given(carsResource.car(carDto.getId())).willReturn(carResource);
        callSuccessWith(callback).when(carResource).delete();

        // Given we fetch the cars after delete
        callSuccessWith(callback, new ArrayList<>()).when(carsResource).getCars();

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

    private <R> ArgumentCaptor<AsyncCallback> captureResourceDelegateCallback(ResourceDelegate<R> delegate,
            R resource) {
        ArgumentCaptor<AsyncCallback> callbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);
        given(delegate.withCallback(callbackCaptor.capture())).willReturn(resource);

        return callbackCaptor;
    }

    private Stubber callSuccessWith(final ArgumentCaptor<AsyncCallback> callbackCaptor) {
        return callSuccessWith(callbackCaptor, null);
    }

    @SuppressWarnings("unchecked")
    private Stubber callSuccessWith(final ArgumentCaptor<AsyncCallback> callbackCaptor, final Object result) {
        return doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                callbackCaptor.getValue().onSuccess(result);
                return null;
            }
        });
    }
}
