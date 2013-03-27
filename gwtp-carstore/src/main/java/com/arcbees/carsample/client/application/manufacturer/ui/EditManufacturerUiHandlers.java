package com.arcbees.carsample.client.application.manufacturer.ui;

import com.arcbees.carsample.shared.domain.Manufacturer;
import com.gwtplatform.mvp.client.UiHandlers;

public interface EditManufacturerUiHandlers extends UiHandlers {
    void createNew();

    void edit(Manufacturer manufacturer);

    void onSave(Manufacturer manufacturer);

    void onCancel();
}
