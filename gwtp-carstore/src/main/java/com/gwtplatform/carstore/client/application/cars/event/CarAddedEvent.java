package com.gwtplatform.carstore.client.application.cars.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.carstore.client.application.cars.event.CarAddedEvent.CarAddedHandler;
import com.gwtplatform.carstore.shared.dto.CarDto;

public class CarAddedEvent extends GwtEvent<CarAddedHandler> {
    public interface CarAddedHandler extends EventHandler {
        void onCarAdded(CarAddedEvent event);
    }

    public static Type<CarAddedHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, CarDto carDto) {
        fire(source, carDto, false);
    }

    public static void fire(HasHandlers source, CarDto carDto, Boolean isNew) {
        source.fireEvent(new CarAddedEvent(carDto, isNew));
    }

    private static final Type<CarAddedHandler> TYPE = new Type<CarAddedHandler>();

    private final CarDto carDto;
    private final Boolean isNew;

    public CarAddedEvent(CarDto carDto) {
        this(carDto, false);
    }

    public CarAddedEvent(CarDto carDto, Boolean isNew) {

        this.carDto = carDto;
        this.isNew = isNew;
    }

    @Override
    public Type<CarAddedHandler> getAssociatedType() {
        return TYPE;
    }

    public CarDto getCar() {
        return carDto;
    }

    public Boolean isNew() {
        return isNew;
    }

    @Override
    protected void dispatch(CarAddedHandler handler) {
        handler.onCarAdded(this);
    }
}
