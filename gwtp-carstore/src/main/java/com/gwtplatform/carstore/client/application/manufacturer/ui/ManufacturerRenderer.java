package com.gwtplatform.carstore.client.application.manufacturer.ui;

import com.google.gwt.text.shared.AbstractRenderer;
import com.gwtplatform.carstore.shared.domain.Manufacturer;

public class ManufacturerRenderer extends AbstractRenderer<Manufacturer> {
    @Override
    public String render(Manufacturer manufacturer) {
        return manufacturer == null ? "" : manufacturer.getName();
    }
}
