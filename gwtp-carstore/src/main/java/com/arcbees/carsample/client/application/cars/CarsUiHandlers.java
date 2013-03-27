package com.arcbees.carsample.client.application.cars;

import com.arcbees.carsample.shared.domain.Car;
import com.gwtplatform.mvp.client.UiHandlers;

public interface CarsUiHandlers extends UiHandlers {
    void onEdit(Car car);

    void onDelete(Car car);

    void onCreate();

    void fetchData(int offset, int limit);
}
