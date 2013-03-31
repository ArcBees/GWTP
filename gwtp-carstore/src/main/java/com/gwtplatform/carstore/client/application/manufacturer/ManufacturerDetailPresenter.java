package com.gwtplatform.carstore.client.application.manufacturer;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.GoBackEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerDetailPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerDetailPresenter.MyView;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerMessages;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.security.LoggedInGatekeeper;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.client.util.SafeAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.DeleteManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.carstore.shared.dispatch.SaveManufacturerAction;
import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class ManufacturerDetailPresenter extends Presenter<MyView, MyProxy> implements GoBackEvent.GoBackHandler,
        ActionBarEvent.ActionBarHandler, ManufacturerDetailUiHandlers {
    public interface MyView extends View, HasUiHandlers<ManufacturerDetailUiHandlers> {
        void edit(Manufacturer manufacturer);

        void getManufacturer();
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.detailManufacturer)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<ManufacturerDetailPresenter> {
    }

    private final DispatchAsync dispatcher;
    private final PlaceManager placeManager;
    private final EditManufacturerMessages messages;

    private Manufacturer currentManufacturer;
    private Boolean createNew;

    @Inject
    public ManufacturerDetailPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
            final DispatchAsync dispatcher, final PlaceManager placeManager,
            final EditManufacturerMessages messages) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.messages = messages;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        String param = request.getParameter("id", null);
        createNew = Strings.isNullOrEmpty(param);

        if (!createNew) {
            Long id = Long.parseLong(param);
            dispatcher.execute(new GetManufacturerAction(id), new SafeAsyncCallback<GetResult<Manufacturer>>() {
                @Override
                public void onSuccess(GetResult<Manufacturer> result) {
                    currentManufacturer = result.getResult();
                    getView().edit(currentManufacturer);
                }
            });
        } else {
            currentManufacturer = new Manufacturer();
            getView().edit(currentManufacturer);
        }
    }

    @Override
    public void onGoBack(GoBackEvent event) {
        placeManager.revealPlace(new PlaceRequest(NameTokens.getManufacturer()));
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.isTheSameToken(NameTokens.getDetailManufacturer())) {
            switch (event.getActionType()) {
                case UPDATE:
                    getView().getManufacturer();
                    break;
                case DONE:
                    getView().getManufacturer();
                    break;
                case DELETE:
                    deleteManufacturer();
                    break;
            }
        }
    }

    @Override
    public void onSave(Manufacturer manufacturer) {
        dispatcher.execute(new SaveManufacturerAction(manufacturer),
                new ErrorHandlerAsyncCallback<GetResult<Manufacturer>>(this) {
            @Override
            public void onSuccess(GetResult<Manufacturer> result) {
                DisplayMessageEvent.fire(ManufacturerDetailPresenter.this,
                        new Message(messages.manufacturerSaved(), MessageStyle.SUCCESS));
                placeManager.revealPlace(new PlaceRequest(NameTokens.getManufacturer()));
            }
        });
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(GoBackEvent.getType(), this);
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @Override
    protected void onReveal() {
        List<ActionType> actions;
        if (createNew) {
            actions = Arrays.asList(new ActionType[] { ActionType.DONE });
            ChangeActionBarEvent.fire(this, actions, false);
        } else {
            actions = Arrays.asList(new ActionType[]{ActionType.DELETE, ActionType.UPDATE});
            ChangeActionBarEvent.fire(this, actions, false);
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    private void deleteManufacturer() {
        Boolean confirm = Window.confirm("Are you sure you want to delete " + currentManufacturer.getName() + "?");
        if (confirm) {
            dispatcher.execute(new DeleteManufacturerAction(currentManufacturer),
                    new ErrorHandlerAsyncCallback<NoResults>(this) {
                @Override
                public void onSuccess(NoResults noResults) {
                    placeManager.revealPlace(new PlaceRequest(NameTokens.getManufacturer()));
                }
            });
        }
    }
}
