package com.arcbees.carsample.shared.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
public class CarProperties extends BaseEntity {
    @OneToOne(optional = false, mappedBy = "carProperties", fetch = FetchType.EAGER)
    private Car car;

    private String someString;

    private Integer someNumber;

    private Date someDate;

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
