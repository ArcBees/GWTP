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

package com.gwtplatform.carstore.client.application.cars.car;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter.MyView;
import com.gwtplatform.carstore.client.application.cars.car.widget.CarPropertiesEditor;
import com.gwtplatform.carstore.client.application.manufacturer.ui.ManufacturerRenderer;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class CarView extends ViewWithUiHandlers<CarUiHandlers> implements MyView, Editor<CarDto> {
    interface Binder extends UiBinder<Widget, CarView> {
    }

    interface Driver extends SimpleBeanEditorDriver<CarDto, CarView> {
    }

    @UiField(provided = true)
    final ValueListBox<ManufacturerDto> manufacturer;
    @UiField(provided = true)
    final CarPropertiesEditor carProperties;

    @UiField
    TextBox model;

    private final Driver driver;

    @Inject
    CarView(Binder uiBinder,
            Driver driver,
            CarPropertiesEditor carProperties) {
        this.driver = driver;
        this.carProperties = carProperties;
        manufacturer = new ValueListBox<>(new ManufacturerRenderer());

        initWidget(uiBinder.createAndBindUi(this));

        driver.initialize(this);
    }

    @Override
    public void edit(CarDto carDto) {
        if (carDto.getManufacturer() == null) {
            carDto.setManufacturer(manufacturer.getValue());
        }

        driver.edit(carDto);
    }

    @Override
    public void setAllowedManufacturers(List<ManufacturerDto> manufacturerDtos) {
        manufacturer.setValue(manufacturerDtos.isEmpty() ? null : manufacturerDtos.get(0));
        manufacturer.setAcceptableValues(manufacturerDtos);
    }

    @Override
    public void resetFields(CarDto carDto) {
        driver.edit(carDto);
    }

    @Override
    public void getCar() {
    }

    @UiHandler("save")
    void onSaveClicked(ClickEvent ignored) {
        getUiHandlers().onSave(driver.flush());
    }

    @UiHandler("close")
    void onCancelClicked(ClickEvent ignored) {
        getUiHandlers().onCancel();
    }
}
