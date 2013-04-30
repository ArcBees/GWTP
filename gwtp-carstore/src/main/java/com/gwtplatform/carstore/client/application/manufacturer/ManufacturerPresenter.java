package com.gwtplatform.carstore.client.application.manufacturer;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerPresenter.MyView;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.security.LoggedInGatekeeper;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.DeleteManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturersAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
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
import com.gwtplatform.mvp.client.proxy.PlaceRequest.Builder;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class ManufacturerPresenter extends Presenter<MyView, MyProxy>
        implements ManufacturerUiHandlers, ActionBarEvent.ActionBarHandler {

    public interface MyView extends View, HasUiHandlers<ManufacturerUiHandlers> {
        void addManufacturer(ManufacturerDto manufacturerDto);

        void displayManufacturers(List<ManufacturerDto> manufacturerDtos);

        void removeManufacturer(ManufacturerDto manufacturerDto);

        void replaceManufacturer(ManufacturerDto oldManufacturer, ManufacturerDto newManufacturer);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.manufacturer)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<ManufacturerPresenter> {
    }

    private final DispatchAsync dispatcher;
    private final PlaceManager placeManager;
    private final EditManufacturerPresenter editManufacturerPresenter;

    private ManufacturerDto editingManufacturer;

    @Inject
    ManufacturerPresenter(EventBus eventBus,
                                 MyView view,
                                 MyProxy proxy,
                                 DispatchAsync dispatcher,
                                 PlaceManager placeManager,
                                 EditManufacturerPresenter editManufacturerPresenter) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.editManufacturerPresenter = editManufacturerPresenter;

        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getManufacturer())) {
            placeManager.revealPlace(new Builder().nameToken(NameTokens.getDetailManufacturer()).build());
        }
    }

    @Override
    public void onDetail(ManufacturerDto manufacturerDto) {
        PlaceRequest placeRequest = new Builder().nameToken(NameTokens.getDetailManufacturer())
                .with("id", String.valueOf(manufacturerDto.getId())).build();

        placeManager.revealPlace(placeRequest);
    }

    @Override
    public void onEdit(ManufacturerDto manufacturerDto) {
        editingManufacturer = manufacturerDto;
        editManufacturerPresenter.edit(manufacturerDto);
    }

    @Override
    public void onCreate() {
        editingManufacturer = null;
        editManufacturerPresenter.createNew();
    }

    @Override
    public void onDelete(final ManufacturerDto manufacturerDto) {
        dispatcher.execute(new DeleteManufacturerAction(manufacturerDto), new ErrorHandlerAsyncCallback<NoResults>(this) {
            @Override
            public void onSuccess(NoResults noResults) {
                getView().removeManufacturer(manufacturerDto);
            }
        });
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(ActionType.ADD), true);

        dispatcher.execute(new GetManufacturersAction(), new SafeAsyncCallback<GetResults<ManufacturerDto>>() {
            @Override
            public void onSuccess(GetResults<ManufacturerDto> result) {
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
        RevealContentEvent.fire(this, ApplicationPresenter.SLOT_MAIN_CONTENT, this);
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
