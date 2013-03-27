package com.arcbees.carsample.client.application.cars.car;

import com.arcbees.carsample.shared.domain.Car;
import com.gwtplatform.mvp.client.UiHandlers;

public interface CarUiHandlers extends UiHandlers {
    void onSave(Car car);

    void onCancel();
}
