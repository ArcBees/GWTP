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
        car.setCarProperties(car.getCarProperties());
        car.setId(car.getId());
        car.setManufacturer(car.getManufacturer());
        car.setModel(car.getModel());
        car.setRatings(car.getRatings());
        
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
        car.setRatings(car.getRatings());
        
        return car;
    }
    
    private Ref<Manufacturer> manufacturer;
    private String model;
    @Load
    private List<Ref<Rating>> ratings;
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

    public List<Rating> getRatings() {
        return Deref.deref(ratings);
    }

    public void setRatings(List<Rating> ratings) {
        for (Rating rating : ratings) {
            if (ratings == null) {
                this.ratings = new ArrayList<Ref<Rating>>();
            }
            this.ratings.add(Ref.create(rating));
        }
    }

    public CarProperties getCarProperties() {
        return Deref.deref(carProperties);
    }

    public void setCarProperties(CarProperties carProperties) {
        if (carProperties != null) {
            this.carProperties = Ref.create(carProperties);
        } else {
            this.carProperties = null;
        }
    }
}
