package com.arcbees.carsample.client.application.manufacturer;

import com.arcbees.carsample.shared.domain.Manufacturer;
import com.gwtplatform.mvp.client.UiHandlers;

public interface ManufacturerDetailUiHandlers extends UiHandlers {
    void onSave(Manufacturer manufacturer);
}
