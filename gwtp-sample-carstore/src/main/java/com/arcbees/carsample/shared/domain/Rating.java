package com.arcbees.carsample.shared.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ratings")
public class Rating implements BaseEntity {
    private static final long serialVersionUID = -5564944651950574107L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private Integer rating;

    public Rating() {
    }

    public Rating(Car car, Integer rating) {
        this.car = car;
        this.rating = rating;
    }

    @Override
    public Integer getId() {
        return id;
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
