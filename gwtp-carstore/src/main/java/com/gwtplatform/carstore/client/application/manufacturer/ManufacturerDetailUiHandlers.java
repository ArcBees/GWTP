package com.gwtplatform.carstore.client.application.manufacturer;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface ManufacturerDetailUiHandlers extends UiHandlers {
    void onSave(ManufacturerDto manufacturerDto);
}
