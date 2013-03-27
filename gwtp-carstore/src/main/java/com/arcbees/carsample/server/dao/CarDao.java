package com.arcbees.carsample.server.dao;

import com.arcbees.carsample.shared.domain.Car;

public class CarDao extends BaseDao<Car> {
    public CarDao() {
        super(Car.class);
    }
}
