package com.arcbees.carsample.client.application.manufacturer;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.ApplicationPresenter;
import com.arcbees.carsample.client.application.event.ActionBarEvent;
import com.arcbees.carsample.client.application.event.ActionBarVisibilityEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent;
import com.arcbees.carsample.client.application.event.ChangeActionBarEvent.ActionType;
import com.arcbees.carsample.client.application.manufacturer.ManufacturerPresenter.MyProxy;
import com.arcbees.carsample.client.application.manufacturer.ManufacturerPresenter.MyView;
import com.arcbees.carsample.client.application.manufacturer.event.ManufacturerAddedEvent;
import com.arcbees.carsample.client.application.manufacturer.ui.EditManufacturerPresenter;
import com.arcbees.carsample.client.place.NameTokens;
import com.arcbees.carsample.client.security.LoggedInGatekeeper;
import com.arcbees.carsample.client.util.ErrorHandlerAsyncCallback;
import com.arcbees.carsample.client.util.SafeAsyncCallback;
import com.arcbees.carsample.shared.dispatch.DeleteManufacturerAction;
import com.arcbees.carsample.shared.dispatch.GetManufacturersAction;
import com.arcbees.carsample.shared.dispatch.GetResults;
import com.arcbees.carsample.shared.dispatch.NoResults;
import com.arcbees.carsample.shared.domain.Manufacturer;
import com.google.web.bindery.event.shared.EventBus;
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

public class ManufacturerPresenter extends Presenter<MyView, MyProxy> implements ManufacturerUiHandlers,
        ActionBarEvent.ActionBarHandler {
    public interface MyView extends View, HasUiHandlers<ManufacturerUiHandlers> {
        void addManufacturer(Manufacturer manufacturer);

        void displayManufacturers(List<Manufacturer> manufacturers);

        void removeManufacturer(Manufacturer manufacturer);

        void replaceManufacturer(Manufacturer oldManufacturer, Manufacturer newManufacturer);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.manufacturer)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<ManufacturerPresenter> {
    }

    private final DispatchAsync dispatcher;
    private final PlaceManager placeManager;
    private final EditManufacturerPresenter editManufacturerPresenter;

    private Manufacturer editingManufacturer;

    @Inject
    public ManufacturerPresenter(EventBus eventBus, MyView view, MyProxy proxy, DispatchAsync dispatcher,
            PlaceManager placeManager, EditManufacturerPresenter editManufacturerPresenter) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.editManufacturerPresenter = editManufacturerPresenter;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getManufacturer())) {
            placeManager.revealPlace(new PlaceRequest(NameTokens.getDetailManufacturer()));
        }
    }

    @Override
    public void onDetail(Manufacturer manufacturer) {
        PlaceRequest placeRequest = new PlaceRequest(NameTokens.getDetailManufacturer());
        placeRequest = placeRequest.with("id", String.valueOf(manufacturer.getId()));
        placeManager.revealPlace(placeRequest);
    }

    @Override
    public void onEdit(Manufacturer manufacturer) {
        editingManufacturer = manufacturer;
        editManufacturerPresenter.edit(manufacturer);
    }

    @Override
    public void onCreate() {
        editingManufacturer = null;
        editManufacturerPresenter.createNew();
    }

    @Override
    public void onDelete(final Manufacturer manufacturer) {
        dispatcher.execute(new DeleteManufacturerAction(manufacturer), new ErrorHandlerAsyncCallback<NoResults>(this) {
            @Override
            public void onSuccess(NoResults noResults) {
                getView().removeManufacturer(manufacturer);
            }
        });
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(new ActionType[] { ActionType.ADD }), true);

        dispatcher.execute(new GetManufacturersAction(), new SafeAsyncCallback<GetResults<Manufacturer>>() {
            @Override
            public void onSuccess(GetResults<Manufacturer> result) {
                getView().displayManufacturers(result.getResults());
            }
        });
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    @ProxyEvent
    void onManufacturerAdded(ManufacturerAddedEvent event) {
        if (editingManufacturer != null) {
            getView().replaceManufacturer(editingManufacturer, event.getManufacturer());
        } else {
            getView().addManufacturer(event.getManufacturer());
        }

        editingManufacturer = event.getManufacturer();
    }
}
