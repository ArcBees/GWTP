package com.arcbees.carsample.client.application.manufacturer.ui;

import com.arcbees.carsample.shared.domain.Manufacturer;
import com.google.gwt.text.shared.AbstractRenderer;

public class ManufacturerRenderer extends AbstractRenderer<Manufacturer> {
    @Override
    public String render(Manufacturer manufacturer) {
        return manufacturer == null ? "" : manufacturer.getName();
    }
}
