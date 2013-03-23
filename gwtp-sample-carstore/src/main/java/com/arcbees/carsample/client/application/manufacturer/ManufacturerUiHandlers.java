package com.arcbees.carsample.client.application.manufacturer;

import com.arcbees.carsample.shared.domain.Manufacturer;
import com.gwtplatform.mvp.client.UiHandlers;

public interface ManufacturerUiHandlers extends UiHandlers {
    void onDetail(Manufacturer manufacturer);

    void onEdit(Manufacturer manufacturer);

    void onDelete(Manufacturer manufacturer);

    void onCreate();
}
