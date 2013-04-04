package com.gwtplatform.carstore.server.dao.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.gwtplatform.carstore.server.dao.objectify.Deref;
import com.gwtplatform.carstore.shared.domain.BaseEntity;
import com.gwtplatform.carstore.shared.dto.CarDto;

@Index
@Entity
public class Car extends BaseEntity {
    public static List<CarDto> createDto(List<Car> cars) {
        if (cars == null) {
            return null;
        }

        List<CarDto> carsDto = new ArrayList<CarDto>();
        for (Car car : cars) {
            carsDto.add(createDto(car));
        }

        return carsDto;
    }

    public static CarDto createDto(Car car) {
        if (car == null) {
            return null;
        }

        CarDto carDto = new CarDto();
        carDto.setCarProperties(CarProperties.createDto(car.getCarProperties()));
        carDto.setId(car.getId());
        carDto.setManufacturer(Manufacturer.createDto(car.getManufacturer()));
        carDto.setModel(car.getModel());

        return carDto;
    }

    public static List<Car> create(List<CarDto> carDtos) {
        if (carDtos == null) {
            return null;
        }

        List<Car> cars = new ArrayList<Car>();
        for (CarDto carDto : carDtos) {
            cars.add(create(carDto));
        }

        return cars;
    }

    public static Car create(CarDto carDto) {
        if (carDto == null) {
            return null;
        }

        Car car = new Car();
        car.setCarProperties(CarProperties.create(carDto.getCarProperties()));
        car.setId(carDto.getId());
        car.setManufacturer(Manufacturer.create(carDto.getManufacturer()));
        car.setModel(carDto.getModel());

        return car;
    }

    private String model;
    @Load
    private Ref<Manufacturer> manufacturer;
    @Load
    private Ref<CarProperties> carProperties;

    public Car() {
        this.model = "";
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public Manufacturer getManufacturer() {
        return Deref.deref(manufacturer);
    }

    public void setManufacturer(Manufacturer manufacturer) {
        if (manufacturer != null) {
            this.manufacturer = Ref.create(manufacturer);
        } else {
            this.manufacturer = null;
        }

    }

    public CarProperties getCarProperties() {
        return Deref.deref(carProperties);
    }

    public void setCarProperties(CarProperties carProperties) {
        if (carProperties != null) {
            try {
                this.carProperties = Ref.create(carProperties);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            this.carProperties = null;
        }
    }
}
