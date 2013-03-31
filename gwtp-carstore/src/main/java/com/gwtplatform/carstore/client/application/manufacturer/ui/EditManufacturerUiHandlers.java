package com.gwtplatform.carstore.client.application.manufacturer.ui;

import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.mvp.client.UiHandlers;

public interface EditManufacturerUiHandlers extends UiHandlers {
    void createNew();

    void edit(Manufacturer manufacturer);

    void onSave(Manufacturer manufacturer);

    void onCancel();
}
