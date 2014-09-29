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

package com.gwtplatform.carstore.client.application.cars;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.cars.CarsPresenter.MyView;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter;
import com.gwtplatform.carstore.client.application.cars.car.CarProxyFactory;
import com.gwtplatform.carstore.client.application.cars.event.CarAddedEvent;
import com.gwtplatform.carstore.client.application.cars.event.CarAddedEvent.CarAddedHandler;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent.ActionBarHandler;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.CarsService;
import com.gwtplatform.carstore.client.util.AbstractAsyncCallback;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.dispatch.rest.client.ResourceDelegate;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest.Builder;

public class CarsPresenter extends Presenter<MyView, MyProxy>
        implements CarsUiHandlers, CarAddedHandler, ActionBarHandler {
    public interface MyView extends View, HasUiHandlers<CarsUiHandlers> {
        void initDataProvider();

        void setCarsCount(Integer result);

        void displayCars(int offset, List<CarDto> cars);

        HasData<CarDto> getCarDisplay();
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.CARS)
    public interface MyProxy extends ProxyPlace<CarsPresenter> {
    }

    private final ResourceDelegate<CarsService> carsDelegate;
    private final PlaceManager placeManager;
    private final CarProxyFactory carProxyFactory;

    @Inject
    CarsPresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy,
            ResourceDelegate<CarsService> carsDelegate,
            PlaceManager placeManager,
            CarProxyFactory carProxyFactory) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN_CONTENT);

        this.carsDelegate = carsDelegate;
        this.placeManager = placeManager;
        this.carProxyFactory = carProxyFactory;

        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getCars())) {
            placeManager.revealPlace(new Builder().nameToken(NameTokens.NEW_CAR).build());
        }
    }

    @Override
    public void onEdit(CarDto carDto) {
        CarPresenter.MyProxy proxy = carProxyFactory.create(carDto,
                carDto.getManufacturer().getName() + carDto.getModel());

        placeManager.revealPlace(new Builder().nameToken(proxy.getNameToken()).build());
    }

    @Override
    public void onCreate() {
        placeManager.revealPlace(new Builder().nameToken(NameTokens.NEW_CAR).build());
    }

    @Override
    public void fetchData(final int offset, int limit) {
        carsDelegate
                .withCallback(new AbstractAsyncCallback<List<CarDto>>() {
                    @Override
                    public void onSuccess(List<CarDto> cars) {
                        getView().displayCars(offset, cars);
                    }
                })
                .getCars(offset, limit);
    }

    @Override
    public void onDelete(CarDto carDto) {
        carsDelegate
                .withCallback(new ErrorHandlerAsyncCallback<Void>(this) {
                    @Override
                    public void onSuccess(Void nothing) {
                        fetchDataForDisplay();

                        getView().setCarsCount(getView().getCarDisplay().getRowCount() - 1);
                    }
                })
                .car(carDto.getId())
                .delete();
    }

    @ProxyEvent
    @Override
    public void onCarAdded(CarAddedEvent event) {
        fetchDataForDisplay();

        if (event.isNew()) {
            getView().setCarsCount(getView().getCarDisplay().getRowCount() + 1);
        }
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(ActionBarEvent.getType(), this);
        carProxyFactory.create(new CarDto(), NameTokens.NEW_CAR);
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(ActionType.ADD), true);
        getView().initDataProvider();

        carsDelegate
                .withCallback(new AbstractAsyncCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer result) {
                        getView().setCarsCount(result);
                    }
                })
                .getCarsCount();
    }

    private void fetchDataForDisplay() {
        Range range = getView().getCarDisplay().getVisibleRange();
        fetchData(range.getStart(), range.getLength());
    }
}
