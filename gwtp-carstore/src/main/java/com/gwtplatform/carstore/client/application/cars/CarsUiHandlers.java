package com.gwtplatform.carstore.client.application.cars;

import com.gwtplatform.carstore.shared.domain.Car;
import com.gwtplatform.mvp.client.UiHandlers;

public interface CarsUiHandlers extends UiHandlers {
    void onEdit(Car car);

    void onDelete(Car car);

    void onCreate();

    void fetchData(int offset, int limit);
}
