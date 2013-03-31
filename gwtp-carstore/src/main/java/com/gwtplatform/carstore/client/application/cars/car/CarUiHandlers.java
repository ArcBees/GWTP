package com.gwtplatform.carstore.client.application.cars.car;

import com.gwtplatform.carstore.shared.domain.Car;
import com.gwtplatform.mvp.client.UiHandlers;

public interface CarUiHandlers extends UiHandlers {
    void onSave(Car car);

    void onCancel();
}
