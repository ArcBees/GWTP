package com.gwtplatform.carstore.client.application.rating;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.client.application.cars.car.CarRenderer;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RatingDetailView extends ViewWithUiHandlers<RatingDetailUiHandlers>
        implements RatingDetailPresenter.MyView, Editor<RatingDto> {
    public interface Binder extends UiBinder<Widget, RatingDetailView> {
    }

    public interface Driver extends SimpleBeanEditorDriver<RatingDto, RatingDetailView> {
    }

    @UiField
    IntegerBox rating;
    @UiField(provided = true)
    ValueListBox<CarDto> car;

    private final Driver driver;

    @Inject
    public RatingDetailView(Binder uiBinder, Driver driver) {
        car = new ValueListBox<CarDto>(new CarRenderer());
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));
        
        driver.initialize(this);

        rating.getElement().setAttribute("placeholder", "Your rating");
    }

    @Override
    public void edit(RatingDto ratingDto) {
        if (ratingDto.getCar() == null) {
            ratingDto.setCar(car.getValue());
        }

        driver.edit(ratingDto);
    }

    @Override
    public void setAllowedCars(List<CarDto> carDtos) {
        car.setValue(carDtos.isEmpty() ? null : carDtos.get(0));
        car.setAcceptableValues(carDtos);
    }

    @Override
    public void getRating() {
        getUiHandlers().onSave(driver.flush());
    }
}
