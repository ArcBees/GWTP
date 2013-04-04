package com.gwtplatform.carstore.server.dao.domain;

import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.gwtplatform.carstore.server.dao.objectify.Deref;
import com.gwtplatform.carstore.shared.domain.BaseEntity;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;

@Index
@Entity
public class CarProperties extends BaseEntity {
    public static CarPropertiesDto createDto(CarProperties carProperties) {
        if (carProperties == null) {
            return null;
        }
        
        CarPropertiesDto carPropertiesDto = new CarPropertiesDto();
        carPropertiesDto.setCar(Car.createDto(carProperties.getCar()));
        carProperties.setId(carProperties.getId());
        carPropertiesDto.setSomeDate(carProperties.getSomeDate());
        carPropertiesDto.setSomeNumber(carProperties.getSomeNumber());
        carPropertiesDto.setSomeString(carProperties.getSomeString());

        return carPropertiesDto;
    }

    public static CarProperties create(CarPropertiesDto carPropertiesDto) {
        if (carPropertiesDto == null) {
            return null;
        }
        
        CarProperties carProperties = new CarProperties();
        carProperties.setCar(Car.create(carPropertiesDto.getCar()));
        carProperties.setId(carPropertiesDto.getId());
        carProperties.setSomeDate(carPropertiesDto.getSomeDate());
        carProperties.setSomeNumber(carPropertiesDto.getSomeNumber());
        carProperties.setSomeString(carPropertiesDto.getSomeString());
        
        return carProperties;
    }

    private String someString;
    private Integer someNumber;
    private Date someDate;
    @Load
    private Ref<Car> car;

    public CarProperties() {
        this.someString = "";
        this.someNumber = 0;
        this.someDate = new Date();
    }

    public CarProperties(String someString, Integer someNumber, Date someDate) {
        this.someString = someString;
        this.someNumber = someNumber;
        this.someDate = someDate;
    }

    public Car getCar() {
        return Deref.deref(car);
    }

    public void setCar(Car car) {
        if (car != null) {
            this.car = Ref.create(car);    
        } else {
            this.car = null;
        }
        
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public Integer getSomeNumber() {
        return someNumber;
    }

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }

    public Date getSomeDate() {
        return someDate;
    }

    public void setSomeDate(Date someDate) {
        this.someDate = someDate;
    }
}
