package com.gwtplatform.carstore.server.dao;

import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;

public class CarDao extends BaseDao<Car> {
    public CarDao() {
        super(Car.class);
    }

    public List<ManufacturerRatingDto> getAverageCarRatingByManufacturer() {
        // TODO: Not implemented yet
        return new ArrayList<ManufacturerRatingDto>();
    }
}
