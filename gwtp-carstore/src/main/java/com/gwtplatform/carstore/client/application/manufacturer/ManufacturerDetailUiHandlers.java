package com.gwtplatform.carstore.client.application.manufacturer;

import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.mvp.client.UiHandlers;

public interface ManufacturerDetailUiHandlers extends UiHandlers {
    void onSave(Manufacturer manufacturer);
}
