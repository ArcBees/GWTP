package com.gwtplatform.carstore.client.application.cars.car;

import com.google.gwt.text.shared.AbstractRenderer;
import com.gwtplatform.carstore.client.application.manufacturer.ui.ManufacturerRenderer;
import com.gwtplatform.carstore.shared.domain.Car;

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
