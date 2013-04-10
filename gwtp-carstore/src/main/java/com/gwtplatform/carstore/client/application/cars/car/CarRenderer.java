package com.gwtplatform.carstore.client.application.cars.car;

import com.google.gwt.text.shared.AbstractRenderer;
import com.gwtplatform.carstore.client.application.manufacturer.ui.ManufacturerRenderer;
import com.gwtplatform.carstore.shared.dto.CarDto;

public class CarRenderer extends AbstractRenderer<CarDto> {
    @Override
    public String render(CarDto carDto) {
        if (carDto == null) {
            return "";
        }

        ManufacturerRenderer manufacturerRenderer = new ManufacturerRenderer();
        String manufacturer = manufacturerRenderer.render(carDto.getManufacturer());

        return manufacturer + " " + carDto.getModel();
    }
}
