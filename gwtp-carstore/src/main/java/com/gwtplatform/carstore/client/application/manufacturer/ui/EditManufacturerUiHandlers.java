package com.gwtplatform.carstore.client.application.manufacturer.ui;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface EditManufacturerUiHandlers extends UiHandlers {
    void createNew();

    void edit(ManufacturerDto manufacturerDto);

    void onSave(ManufacturerDto manufacturerDto);

    void onCancel();
}
