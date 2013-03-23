package com.arcbees.carsample.client.application.rating;

import java.util.List;

import javax.inject.Inject;

import com.arcbees.carsample.client.application.cars.car.CarRenderer;
import com.arcbees.carsample.shared.domain.Car;
import com.arcbees.carsample.shared.domain.Rating;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingDetailView extends ViewWithUiHandlers<RatingDetailUiHandlers>
        implements RatingDetailPresenter.MyView, Editor<Rating> {
    public interface Binder extends UiBinder<Widget, RatingDetailView> {
    }

    public interface Driver extends SimpleBeanEditorDriver<Rating, RatingDetailView> {
    }

    @UiField
    IntegerBox rating;
    @UiField(provided = true)
    ValueListBox<Car> car;

    private final Driver driver;

    @Inject
    public RatingDetailView(Binder uiBinder, Driver driver) {
        car = new ValueListBox<Car>(new CarRenderer());
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));
        
        driver.initialize(this);

        rating.getElement().setAttribute("placeholder", "Your rating");
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

    @Override
    public void getRating() {
        getUiHandlers().onSave(driver.flush());
    }
}
