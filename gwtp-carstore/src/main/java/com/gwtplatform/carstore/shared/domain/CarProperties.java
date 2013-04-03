package com.gwtplatform.carstore.shared.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Index
@Entity
public class CarProperties extends BaseEntity {
    private String someString;
    private Integer someNumber;
    private Date someDate;
    @Embed
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
