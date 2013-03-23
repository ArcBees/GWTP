package com.arcbees.carsample.client.application.cars.event;

import com.arcbees.carsample.client.application.cars.event.CarAddedEvent.CarAddedHandler;
import com.arcbees.carsample.shared.domain.Car;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class CarAddedEvent extends GwtEvent<CarAddedHandler> {
    public interface CarAddedHandler extends EventHandler {
        void onCarAdded(CarAddedEvent event);
    }

    public static Type<CarAddedHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, Car car) {
        fire(source, car, false);
    }

    public static void fire(HasHandlers source, Car car, Boolean isNew) {
        source.fireEvent(new CarAddedEvent(car, isNew));
    }

    private static final Type<CarAddedHandler> TYPE = new Type<CarAddedHandler>();

    private final Car car;
    private final Boolean isNew;

    public CarAddedEvent(Car car) {
        this(car, false);
    }

    public CarAddedEvent(Car car, Boolean isNew) {

        this.car = car;
        this.isNew = isNew;
    }

    @Override
    public Type<CarAddedHandler> getAssociatedType() {
        return TYPE;
    }

    public Car getCar() {
        return car;
    }

    public Boolean isNew() {
        return isNew;
    }

    @Override
    protected void dispatch(CarAddedHandler handler) {
        handler.onCarAdded(this);
    }
}
