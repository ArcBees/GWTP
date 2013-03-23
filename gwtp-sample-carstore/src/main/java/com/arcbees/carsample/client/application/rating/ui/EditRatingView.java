package com.arcbees.carsample.client.application.rating.ui;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.cars.car.CarRenderer;
import com.arcbees.carsample.client.application.rating.ui.EditRatingPresenter.MyView;
import com.arcbees.carsample.shared.domain.Car;
import com.arcbees.carsample.shared.domain.Rating;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class EditRatingView extends PopupViewWithUiHandlers<EditRatingUiHandlers> implements MyView, Editor<Rating> {
    public interface Binder extends UiBinder<Widget, EditRatingView> {
    }

    public interface Driver extends SimpleBeanEditorDriver<Rating, EditRatingView> {
    }

    @UiField
    IntegerBox rating;
    @UiField(provided = true)
    ValueListBox<Car> car;

    private final Driver driver;

    @Inject
    public EditRatingView(Binder uiBinder, Driver driver, EventBus eventBus) {
        super(eventBus);

        car = new ValueListBox<Car>(new CarRenderer());
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));
        
        driver.initialize(this);
    }

    @Override
    public void edit(Rating rating) {
        if (rating.getCar() == null) {
            rating.setCar(car.getValue());
        }

        driver.edit(rating);
    }

    @Override
    public void setAllowedCars(List<Car> cars) {
        car.setValue(cars.isEmpty() ? null : cars.get(0));
        car.setAcceptableValues(cars);
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
