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
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.GoBackEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.DeleteCarAction;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturersAction;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.carstore.shared.dispatch.SaveCarAction;
import com.gwtplatform.carstore.shared.domain.Car;
import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.dispatch.shared.DispatchAsync;
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
        void edit(Car car);

        void setAllowedManufacturers(List<Manufacturer> manufacturers);

        void resetFields(Car car);

        void getCar();
    }

    public interface MyProxy extends ProxyPlace<CarPresenter> {
    }

    private final CarMessages messages;
    private final DispatchAsync dispatcher;
    private final PlaceManager placeManager;
    private final CarProxyFactory carProxyFactory;

    private Car car;
    private Boolean createNew;

    @Inject
    public CarPresenter(final EventBus eventBus, final MyView view, final DispatchAsync dispatcher,
            final PlaceManager placeManager, final CarProxyFactory carProxyFactory, final CarMessages messages,
            @Assisted final MyProxy proxy, @Assisted final Car car) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.messages = messages;
        this.placeManager = placeManager;
        this.carProxyFactory = carProxyFactory;
        this.car = car != null ? car : new Car();
        
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
        } else if (event.isTheSameToken(car.getManufacturer().getName() + car.getModel())) {
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
    public void onSave(final Car car) {
        dispatcher.execute(new SaveCarAction(car), new ErrorHandlerAsyncCallback<GetResult<Car>>(this) {
            @Override
            public void onSuccess(GetResult<Car> result) {
                onCarSaved(car, result.getResult());
            }
        });
    }

    @Override
    public String getName() {
        if (car.getId() != null) {
            return car.getManufacturer().getName() + " " + car.getModel();
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
        dispatcher.execute(new GetManufacturersAction(), new SafeAsyncCallback<GetResults<Manufacturer>>() {
            @Override
            public void onSuccess(GetResults<Manufacturer> result) {
                onGetManufacturerSuccess(result.getResults());
            }
        });

        createNew = placeManager.getCurrentPlaceRequest().matchesNameToken(NameTokens.newCar);
        List<ActionType> actions;
        if (createNew) {
            actions = Arrays.asList(new ActionType[]{ActionType.DONE});
            ChangeActionBarEvent.fire(this, actions, false);
        } else {
            actions = Arrays.asList(new ActionType[]{ActionType.DELETE, ActionType.UPDATE});
            ChangeActionBarEvent.fire(this, actions, false);
        }

        NavigationTabEvent.fireReveal(this, this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, RootCarPresenter.TYPE_SetCarContent, this);
    }

    private void onGetManufacturerSuccess(List<Manufacturer> manufacturers) {
        getView().setAllowedManufacturers(manufacturers);
        getView().edit(car);
    }

    private void onCarSaved(Car oldCar, Car newCar) {
        DisplayMessageEvent.fire(CarPresenter.this, new Message(messages.carSaved(), MessageStyle.SUCCESS));
        CarAddedEvent.fire(CarPresenter.this, newCar, oldCar.getId() == null);

        if (oldCar.getId() == null) {
            car = new Car();
            getView().resetFields(car);

            CarPresenter.MyProxy proxy = carProxyFactory.create(newCar, newCar.getManufacturer().getName() +
                    newCar.getModel());

            placeManager.revealPlace(new PlaceRequest(proxy.getNameToken()));
        }
    }

    private void onDeleteCar() {
        Boolean confirm = Window.confirm("Are you sure you want to delete " + car.getModel() + "?");
        if (confirm) {
            dispatcher.execute(new DeleteCarAction(car), new ErrorHandlerAsyncCallback<NoResults>(this) {
                @Override
                public void onSuccess(NoResults noResults) {
                    NavigationTabEvent.fireClose(CarPresenter.this, CarPresenter.this);
                }
            });
        }
    }
}
