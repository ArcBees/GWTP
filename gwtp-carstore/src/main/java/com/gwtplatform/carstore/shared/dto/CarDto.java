package com.gwtplatform.carstore.shared.dto;

import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.carstore.shared.domain.BaseEntity;

public class CarDto extends BaseEntity {
    private ManufacturerDto manufacturer;
    private String model;
    private List<RatingDto> ratings;
    private CarPropertiesDto carProperties;

    public CarDto() {
        this.model = "";
        this.carProperties = new CarPropertiesDto();
        this.ratings = new ArrayList<RatingDto>();
    }

    public CarDto(String model, ManufacturerDto manufacturer, CarPropertiesDto carProperties) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.carProperties = carProperties;
        this.ratings = new ArrayList<RatingDto>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerDto manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<RatingDto> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingDto> ratings) {
        this.ratings = ratings;
    }

    public CarPropertiesDto getCarProperties() {
        return carProperties;
    }

    public void setCarProperties(CarPropertiesDto carProperties) {
        this.carProperties = carProperties;
    }
}
