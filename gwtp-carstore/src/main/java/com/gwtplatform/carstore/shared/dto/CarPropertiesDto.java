package com.gwtplatform.carstore.shared.dto;

import java.util.Date;

import com.gwtplatform.carstore.shared.domain.BaseEntity;

public class CarPropertiesDto extends BaseEntity {
    private String someString;
    private Integer someNumber;
    private Date someDate;
    private CarDto carDto;

    public CarPropertiesDto() {
        this.someString = "";
        this.someNumber = 0;
        this.someDate = new Date();
    }

    public CarPropertiesDto(String someString, Integer someNumber, Date someDate) {
        this.someString = someString;
        this.someNumber = someNumber;
        this.someDate = someDate;
    }

    public CarDto getCar() {
        return carDto;
    }

    public void setCar(CarDto carDto) {
        this.carDto = carDto;
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
