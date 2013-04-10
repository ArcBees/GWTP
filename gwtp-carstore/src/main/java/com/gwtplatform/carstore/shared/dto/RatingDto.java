package com.gwtplatform.carstore.shared.dto;

import com.gwtplatform.carstore.shared.domain.BaseEntity;

public class RatingDto extends BaseEntity {
    private Integer rating;
    private CarDto carDto;

    public RatingDto() {
    }

    public RatingDto(CarDto carDto, Integer rating) {
        this.carDto = carDto;
        this.rating = rating;
    }

    public CarDto getCar() {
        return carDto;
    }

    public void setCar(final CarDto carDto) {
        this.carDto = carDto;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        if (carDto != null && carDto.getManufacturer() != null) {
            ManufacturerDto manufacturerDto = carDto.getManufacturer();
            return manufacturerDto.getName() + "/" + carDto.getModel();
        }

        return super.toString();
    }
}
