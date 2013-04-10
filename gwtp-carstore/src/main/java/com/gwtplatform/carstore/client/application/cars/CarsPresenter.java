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
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.security.LoggedInGatekeeper;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.DeleteCarAction;
import com.gwtplatform.carstore.shared.dispatch.GetCarsAction;
import com.gwtplatform.carstore.shared.dispatch.GetCarsCountAction;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.NumberDto;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class CarsPresenter extends Presenter<MyView, MyProxy> implements CarsUiHandlers,
        CarAddedEvent.CarAddedHandler, ActionBarEvent.ActionBarHandler {
    public interface MyView extends View, HasUiHandlers<CarsUiHandlers> {
        void initDataProvider();

        void setCarsCount(Integer result);

        void displayCars(int offset, List<CarDto> carDtos);

        HasData<CarDto> getCarDisplay();
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.cars)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<CarsPresenter> {
    }

    private final DispatchAsync dispatcher;
    private final PlaceManager placeManager;
    private final CarProxyFactory carProxyFactory;

    @Inject
    public CarsPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
            final DispatchAsync dispatcher, final PlaceManager placeManager, final CarProxyFactory carProxyFactory) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.carProxyFactory = carProxyFactory;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getCars())) {
            placeManager.revealPlace(new PlaceRequest(NameTokens.newCar));
        }
    }

    @Override
    public void onEdit(CarDto carDto) {
        CarPresenter.MyProxy proxy = carProxyFactory.create(carDto, carDto.getManufacturer().getName() + carDto.getModel());

        placeManager.revealPlace(new PlaceRequest(proxy.getNameToken()));
    }

    @Override
    public void onCreate() {
        placeManager.revealPlace(new PlaceRequest(NameTokens.newCar));
    }

    @Override
    public void fetchData(final int offset, int limit) {
        dispatcher.execute(new GetCarsAction(offset, limit), new SafeAsyncCallback<GetResults<CarDto>>() {
            @Override
            public void onSuccess(GetResults<CarDto> result) {
                getView().displayCars(offset, result.getResults());
            }
        });
    }

    @Override
    public void onDelete(final CarDto carDto) {
        dispatcher.execute(new DeleteCarAction(carDto), new ErrorHandlerAsyncCallback<NoResults>(this) {
            @Override
            public void onSuccess(NoResults noResults) {
                fetchDataForDisplay();

                getView().setCarsCount(getView().getCarDisplay().getRowCount() - 1);
            }
        });
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
        carProxyFactory.create(new CarDto(), NameTokens.newCar);
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(new ActionType[]{ActionType.ADD}), true);
        getView().initDataProvider();

        dispatcher.execute(new GetCarsCountAction(), new SafeAsyncCallback<GetResult<NumberDto<Integer>>>() {
            @Override
            public void onSuccess(GetResult<NumberDto<Integer>> result) {
                getView().setCarsCount(result.getResult().getNumber());
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    private void fetchDataForDisplay() {
        Range range = getView().getCarDisplay().getVisibleRange();
        fetchData(range.getStart(), range.getLength());
    }
}
