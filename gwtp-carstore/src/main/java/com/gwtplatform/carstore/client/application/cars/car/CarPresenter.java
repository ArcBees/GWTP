package com.gwtplatform.carstore.client.application.cars.car;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter.MyView;
import com.gwtplatform.carstore.client.application.cars.car.navigation.NavigationTab;
import com.gwtplatform.carstore.client.application.cars.car.navigation.NavigationTabEvent;
import com.gwtplatform.carstore.client.application.cars.event.CarAddedEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.GoBackEvent;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.CarService;
import com.gwtplatform.carstore.client.rest.ManufacturerService;
import com.gwtplatform.carstore.client.util.AbstractAsyncCallback;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class CarPresenter extends Presenter<MyView, CarPresenter.MyProxy> implements CarUiHandlers,
        NavigationTab, GoBackEvent.GoBackHandler, ActionBarEvent.ActionBarHandler {
    public interface MyView extends View, HasUiHandlers<CarUiHandlers> {
        void edit(CarDto carDto);

        void setAllowedManufacturers(List<ManufacturerDto> manufacturerDtos);

        void resetFields(CarDto carDto);

        void getCar();
    }

    public interface MyProxy extends ProxyPlace<CarPresenter> {
    }

    private final CarService carService;
    private final ManufacturerService manufacturerService;
    private final CarMessages messages;
    private final DispatchAsync dispatcher;
    private final PlaceManager placeManager;
    private final CarProxyFactory carProxyFactory;

    private CarDto carDto;

    @Inject
    public CarPresenter(
            EventBus eventBus,
            MyView view,
            DispatchAsync dispatcher,
            CarService carService,
            ManufacturerService manufacturerService,
            PlaceManager placeManager,
            CarProxyFactory carProxyFactory,
            CarMessages messages,
            @Assisted MyProxy proxy,
            @Assisted CarDto carDto) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.carService = carService;
        this.manufacturerService = manufacturerService;
        this.messages = messages;
        this.placeManager = placeManager;
        this.carProxyFactory = carProxyFactory;
        this.carDto = carDto != null ? carDto : new CarDto();

        getView().setUiHandlers(this);
    }

    @Override
    public void onGoBack(GoBackEvent event) {
        placeManager.revealPlace(new PlaceRequest(NameTokens.getCars()));
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.isTheSameToken(NameTokens.newCar)) {
            if (event.getActionType() == ActionType.DONE) {
                getView().getCar();
            }
        } else if (event.isTheSameToken(carDto.getManufacturer().getName() + carDto.getModel())) {
            if (event.getActionType() == ActionType.UPDATE) {
                getView().getCar();
            } else if (event.getActionType() == ActionType.DELETE) {
                onDeleteCar();
            }
        }
    }

    @Override
    public void onCancel() {
        NavigationTabEvent.fireClose(this, this);
    }

    @Override
    public void onSave(final CarDto carDto) {
        Action<GetResult<CarDto>> action;
        if (carDto.isSaved()) {
            action = carService.save(carDto.getId(), carDto);
        } else {
            action = carService.create(carDto);
        }
        dispatcher.execute(action, new ErrorHandlerAsyncCallback<GetResult<CarDto>>(this) {
            @Override
            public void onSuccess(GetResult<CarDto> result) {
                onCarSaved(carDto, result.getResult());
            }
        });
    }

    @Override
    public String getName() {
        if (carDto.getId() != null) {
            return carDto.getManufacturer().getName() + " " + carDto.getModel();
        } else {
            return "New car";
        }
    }

    @Override
    public String getToken() {
        return getProxy().getNameToken();
    }

    @Override
    public boolean isClosable() {
        return true;
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(GoBackEvent.getType(), this);
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @Override
    protected void onReveal() {
        dispatcher.execute(manufacturerService.getManufacturers(),
                new AbstractAsyncCallback<GetResults<ManufacturerDto>>() {
                    @Override
                    public void onSuccess(GetResults<ManufacturerDto> result) {
                        onGetManufacturerSuccess(result.getResults());
                    }
                });

        Boolean createNew = placeManager.getCurrentPlaceRequest().matchesNameToken(NameTokens.newCar);
        List<ActionType> actions;
        if (createNew) {
            actions = Arrays.asList(ActionType.DONE);
            ChangeActionBarEvent.fire(this, actions, false);
        } else {
            actions = Arrays.asList(ActionType.DELETE, ActionType.UPDATE);
            ChangeActionBarEvent.fire(this, actions, false);
        }

        NavigationTabEvent.fireReveal(this, this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, RootCarPresenter.TYPE_SetCarContent, this);
    }

    private void onGetManufacturerSuccess(List<ManufacturerDto> manufacturerDtos) {
        getView().setAllowedManufacturers(manufacturerDtos);
        getView().edit(carDto);
    }

    private void onCarSaved(CarDto oldCar, CarDto newCar) {
        DisplayMessageEvent.fire(CarPresenter.this, new Message(messages.carSaved(), MessageStyle.SUCCESS));
        CarAddedEvent.fire(CarPresenter.this, newCar, oldCar.getId() == null);

        if (oldCar.getId() == null) {
            carDto = new CarDto();
            getView().resetFields(carDto);

            MyProxy proxy = carProxyFactory.create(newCar, newCar.getManufacturer().getName() + newCar.getModel());

            placeManager.revealPlace(new PlaceRequest(proxy.getNameToken()));
        }
    }

    private void onDeleteCar() {
        Boolean confirm = Window.confirm("Are you sure you want to delete " + carDto.getModel() + "?");
        if (confirm) {
            dispatcher.execute(carService.delete(carDto.getId()),
                    new ErrorHandlerAsyncCallback<NoResult>(this) {
                        @Override
                        public void onSuccess(NoResult noResult) {
                            NavigationTabEvent.fireClose(CarPresenter.this, CarPresenter.this);
                        }
                    });
        }
    }
}
