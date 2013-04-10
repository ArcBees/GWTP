package com.gwtplatform.carstore.client.application.cars.car;

import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface CarUiHandlers extends UiHandlers {
    void onSave(CarDto carDto);

    void onCancel();
}
