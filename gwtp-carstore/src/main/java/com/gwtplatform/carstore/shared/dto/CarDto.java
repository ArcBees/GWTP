package com.gwtplatform.carstore.shared.dto;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.gwtplatform.carstore.shared.domain.BaseEntity;

public class CarDto extends BaseEntity {
    private ManufacturerDto manufacturerDto;
    private String model;
    private List<RatingDto> ratingDtos;
    private CarPropertiesDto carPropertiesDto;

    public CarDto() {
        this.model = "";
        this.carPropertiesDto = new CarPropertiesDto();
        this.ratingDtos = new ArrayList<RatingDto>();
    }

    public CarDto(String model, ManufacturerDto manufacturerDto, CarPropertiesDto carPropertiesDto) {
        this.model = model;
        this.manufacturerDto = manufacturerDto;
        this.carPropertiesDto = carPropertiesDto;
        this.ratingDtos = new ArrayList<RatingDto>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturerDto;
    }

    public void setManufacturer(final ManufacturerDto manufacturerDto) {
        this.manufacturerDto = manufacturerDto;
    }

    public List<RatingDto> getRatings() {
        return ratingDtos;
    }

    public void setRatings(List<RatingDto> ratingDtos) {
        this.ratingDtos = ratingDtos;
    }

    public CarPropertiesDto getCarProperties() {
        return carPropertiesDto;
    }

    public void setCarProperties(CarPropertiesDto carPropertiesDto) {
        this.carPropertiesDto = carPropertiesDto;
    }
}
