package com.arcbees.carsample.client.application.cars.car;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.cars.car.CarPresenter.MyView;
import com.arcbees.carsample.client.application.cars.car.widget.CarPropertiesEditor;
import com.arcbees.carsample.client.application.manufacturer.ui.ManufacturerRenderer;
import com.arcbees.carsample.shared.domain.Car;
import com.arcbees.carsample.shared.domain.Manufacturer;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class CarView extends ViewWithUiHandlers<CarUiHandlers> implements MyView, Editor<Car> {
    public interface Binder extends UiBinder<Widget, CarView> {
    }

    public interface Driver extends SimpleBeanEditorDriver<Car, CarView> {
    }

    @UiField
    TextBox model;
    @UiField(provided = true)
    ValueListBox<Manufacturer> manufacturer;
    @UiField
    CarPropertiesEditor carProperties;

    private final Driver driver;

    @Inject
    public CarView(Binder uiBinder, Driver driver) {
        manufacturer = new ValueListBox<Manufacturer>(new ManufacturerRenderer());
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));
        
        driver.initialize(this);
    }

    @Override
    public void edit(Car car) {
        if (car.getManufacturer() == null) {
            car.setManufacturer(manufacturer.getValue());
        }

        driver.edit(car);
    }

    @Override
    public void setAllowedManufacturers(List<Manufacturer> manufacturers) {
        manufacturer.setValue(manufacturers.isEmpty() ? null : manufacturers.get(0));
        manufacturer.setAcceptableValues(manufacturers);
    }

    @Override
    public void resetFields(Car car) {
        driver.edit(car);
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
