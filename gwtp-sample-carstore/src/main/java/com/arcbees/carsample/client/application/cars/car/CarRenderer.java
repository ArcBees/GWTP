package com.arcbees.carsample.client.application.cars.car;

import com.arcbees.carsample.client.application.manufacturer.ui.ManufacturerRenderer;
import com.arcbees.carsample.shared.domain.Car;
import com.google.gwt.text.shared.AbstractRenderer;

public class CarRenderer extends AbstractRenderer<Car> {
    @Override
    public String render(Car car) {
        if (car == null) {
            return "";
        }

        ManufacturerRenderer manufacturerRenderer = new ManufacturerRenderer();
        String manufacturer = manufacturerRenderer.render(car.getManufacturer());

        return manufacturer + " " + car.getModel();
    }
}
