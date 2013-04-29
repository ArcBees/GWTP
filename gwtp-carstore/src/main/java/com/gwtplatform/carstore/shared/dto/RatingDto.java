package com.gwtplatform.carstore.shared.dto;

import com.gwtplatform.carstore.shared.domain.BaseEntity;

public class RatingDto extends BaseEntity {
    private Integer rating;
    private CarDto car;

    public RatingDto() {
    }

    public RatingDto(CarDto car, Integer rating) {
        this.car = car;
        this.rating = rating;
    }

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto car) {
        this.car = car;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        if (car != null && car.getManufacturer() != null) {
            ManufacturerDto manufacturerDto = car.getManufacturer();
            return manufacturerDto.getName() + "/" + car.getModel();
        }

        return super.toString();
    }
}
