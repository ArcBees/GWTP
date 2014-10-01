/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.server.api;

import java.util.List;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.CarPropertiesDao;
import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.shared.api.CarResource;
import com.gwtplatform.carstore.shared.api.CarsResource;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;

public class CarsResourceImpl implements CarsResource {
    private static final Integer INT_DEFAULT_LIMIT = Integer.valueOf(DEFAULT_LIMIT);
    private static final Integer INT_DEFAULT_OFFSET = Integer.valueOf(DEFAULT_OFFSET);

    private final ResourcesFactory resourcesFactory;
    private final CarDao carDao;
    private final CarPropertiesDao carPropertiesDao;

    @Inject
    CarsResourceImpl(
            ResourcesFactory resourcesFactory,
            CarDao carDao,
            CarPropertiesDao carPropertiesDao) {
        this.resourcesFactory = resourcesFactory;
        this.carDao = carDao;
        this.carPropertiesDao = carPropertiesDao;
    }

    @Override
    public List<CarDto> getCars() {
        return getCars(INT_DEFAULT_OFFSET, INT_DEFAULT_LIMIT);
    }

    @Override
    public List<CarDto> getCars(int offset, int limit) {
        List<CarDto> cars;

        if (offset == INT_DEFAULT_OFFSET && limit == INT_DEFAULT_LIMIT) {
            cars = Car.createDto(carDao.getAll());
        } else {
            cars = Car.createDto(carDao.getSome(offset, limit));
        }

        return cars;
    }

    @Override
    public int getCarsCount() {
        return carDao.countAll();
    }

    @Override
    public CarDto saveOrCreate(CarDto carDto) {
        CarPropertiesDto carProperties = carDto.getCarProperties();
        if (carProperties != null && !carProperties.isSaved()) {
            carProperties = carPropertiesDao.put(carProperties);
            carDto.setCarProperties(carProperties);
        }

        Car car = carDao.put(Car.create(carDto));

        return Car.createDto(car);
    }

    @Override
    public CarResource car(Long carId) {
        return resourcesFactory.createCarResource(carId);
    }
}
