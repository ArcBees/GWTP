package com.gwtplatform.carstore.client.application.manufacturer.ui;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter.MyView;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveManufacturerAction;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;

public class EditManufacturerPresenter extends PresenterWidget<MyView> implements EditManufacturerUiHandlers {
    public interface MyView extends PopupView, HasUiHandlers<EditManufacturerUiHandlers> {
        void edit(ManufacturerDto manufacturerDto);
    }

    private final DispatchAsync dispatcher;
    private ManufacturerDto manufacturerDto;
    private final EditManufacturerMessages messages;

    @Inject
    public EditManufacturerPresenter(EventBus eventBus, MyView view, DispatchAsync dispatcher,
            EditManufacturerMessages messages) {
        super(eventBus, view);
        
        this.dispatcher = dispatcher;
        this.messages = messages;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void createNew() {
        manufacturerDto = new ManufacturerDto();

        reveal();
    }

    @Override
    public void onCancel() {
        getView().hide();
    }

    @Override
    public void edit(ManufacturerDto manufacturerDto) {
        this.manufacturerDto = manufacturerDto;

        reveal();
    }

    @Override
    public void onSave(ManufacturerDto manufacturerDto) {
        dispatcher.execute(new SaveManufacturerAction(manufacturerDto),
                new ErrorHandlerAsyncCallback<GetResult<ManufacturerDto>>(this) {
                    @Override
                    public void onSuccess(GetResult<ManufacturerDto> result) {
                        DisplayMessageEvent.fire(EditManufacturerPresenter.this,
                                new Message(messages.manufacturerSaved(), MessageStyle.SUCCESS));
                        ManufacturerAddedEvent.fire(EditManufacturerPresenter.this, result.getResult());

                        getView().hide();
                    }
                });
    }

    private void reveal() {
        getView().edit(manufacturerDto);

        RevealRootPopupContentEvent.fire(this, this);
    }
}
