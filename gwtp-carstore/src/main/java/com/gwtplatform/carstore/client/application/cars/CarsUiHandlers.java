package com.gwtplatform.carstore.client.application.cars;

import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface CarsUiHandlers extends UiHandlers {
    void onEdit(CarDto carDto);

    void onDelete(CarDto carDto);

    void onCreate();

    void fetchData(int offset, int limit);
}
