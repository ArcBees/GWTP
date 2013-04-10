package com.gwtplatform.carstore.client.application.manufacturer.ui;

import com.google.gwt.text.shared.AbstractRenderer;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

public class ManufacturerRenderer extends AbstractRenderer<ManufacturerDto> {
    @Override
    public String render(ManufacturerDto manufacturerDto) {
        return manufacturerDto == null ? "" : manufacturerDto.getName();
    }
}
