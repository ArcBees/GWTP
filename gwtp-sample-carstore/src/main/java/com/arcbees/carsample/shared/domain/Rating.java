package com.arcbees.carsample.shared.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class Rating extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Car car;

    private Integer rating;

    public Rating() {
    }

    public Rating(Car car, Integer rating) {
        this.car = car;
        this.rating = rating;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(final Car car) {
        this.car = car;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        if (car != null && car.getManufacturer() != null) {
            Manufacturer manufacturer = car.getManufacturer();
            return manufacturer.getName() + "/" + car.getModel();
        }

        return super.toString();
    }
}
