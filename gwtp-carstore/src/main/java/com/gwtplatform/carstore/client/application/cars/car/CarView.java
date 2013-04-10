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
    public interface Binder extends UiBinder<Widget, CarView> {
    }

    public interface Driver extends SimpleBeanEditorDriver<CarDto, CarView> {
    }

    @UiField
    TextBox model;
    @UiField(provided = true)
    ValueListBox<ManufacturerDto> manufacturer;
    @UiField
    CarPropertiesEditor carProperties;

    private final Driver driver;

    @Inject
    public CarView(Binder uiBinder, Driver driver) {
        manufacturer = new ValueListBox<ManufacturerDto>(new ManufacturerRenderer());
        this.driver = driver;

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
