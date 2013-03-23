package com.arcbees.carsample.server.dao;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.arcbees.carsample.shared.domain.Car;

public class CarDao extends BaseDao<Car> {
    @Inject
    public CarDao(Provider<EntityManager> entityManagerProvider) {
        super(Car.class, entityManagerProvider);
    }
}
