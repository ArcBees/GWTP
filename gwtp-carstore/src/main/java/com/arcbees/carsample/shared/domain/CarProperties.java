package com.arcbees.carsample.shared.domain;

import java.util.Date;

import javax.persistence.Embedded;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class CarProperties extends BaseEntity {
    private String someString;
    private Integer someNumber;
    private Date someDate;
    @Embedded
    private Car car;

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
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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
