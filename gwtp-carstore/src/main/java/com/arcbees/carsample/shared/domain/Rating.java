package com.arcbees.carsample.shared.domain;

import javax.persistence.Entity;

@Entity
public class Rating extends BaseEntity {
    private Integer rating;
    private Car car;

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
