/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.manufacturer.ui;

import org.turbogwt.mvp.databind.client.Binding;
import org.turbogwt.mvp.databind.client.BindingImpl;
import org.turbogwt.mvp.databind.client.DatabindView;
import org.turbogwt.ext.gwtp.databind.client.PopupDatabindView;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent;
import com.gwtplatform.carstore.client.application.manufacturer.properties.ManufacturerDtoProperties;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter.MyView;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.resources.EditManufacturerMessages;
import com.gwtplatform.carstore.client.rest.ManufacturerService;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;


import javax.inject.Inject;

public class EditManufacturerPresenter extends PresenterWidget<MyView> implements EditManufacturerUiHandlers {

    public interface MyView extends PopupDatabindView<EditManufacturerUiHandlers> {
    }

    private final RestDispatch dispatcher;
    private final ManufacturerService manufacturerService;
    private final EditManufacturerMessages messages;
    private final Binding<ManufacturerDto> binding;

    @Inject
    public EditManufacturerPresenter(EventBus eventBus,
                                     MyView view,
                                     RestDispatch dispatcher,
                                     ManufacturerService manufacturerService,
                                     EditManufacturerMessages messages) {
        super(eventBus, view);

        this.dispatcher = dispatcher;
        this.manufacturerService = manufacturerService;
        this.messages = messages;
        this.binding = new BindingImpl<ManufacturerDto>(view);

        getView().setUiHandlers(this);
    }

    public void createNew() {
        binding.setModel(new ManufacturerDto());

        reveal();
    }

    @Override
    public void onCancel() {
        getView().hide();
    }

    public void edit(ManufacturerDto manufacturerDto) {
        binding.setModel(manufacturerDto);

        reveal();
    }

    @Override
    public void onSave() {
        dispatcher.execute(manufacturerService.saveOrCreate(binding.getModel()),
                new ErrorHandlerAsyncCallback<ManufacturerDto>(this) {
                    @Override
                    public void onSuccess(ManufacturerDto savedManufacturer) {
                        DisplayMessageEvent.fire(EditManufacturerPresenter.this,
                                new Message(messages.manufacturerSaved(), MessageStyle.SUCCESS));
                        ManufacturerAddedEvent.fire(EditManufacturerPresenter.this, savedManufacturer);

                        getView().hide();
                    }
                });
    }

    @Override
    public void onValueChanged(String id, Object value) {
        binding.onValueChanged(id, value);
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(binding.bind("name", ManufacturerDtoProperties.NAME));
    }

    private void reveal() {
        RevealRootPopupContentEvent.fire(this, this);
    }
}
