package com.gwtplatform.carstore.client.application.manufacturer;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface ManufacturerUiHandlers extends UiHandlers {
    void onDetail(ManufacturerDto manufacturerDto);

    void onEdit(ManufacturerDto manufacturerDto);

    void onDelete(ManufacturerDto manufacturerDto);

    void onCreate();
}
