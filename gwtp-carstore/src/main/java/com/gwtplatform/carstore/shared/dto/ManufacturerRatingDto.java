package com.gwtplatform.carstore.shared.dto;

public class ManufacturerRatingDto implements Dto {
    String manufacturer;
    Double rating;

    protected ManufacturerRatingDto() {
    }

    public ManufacturerRatingDto(String manufacturer, Double rating) {
        this.manufacturer = manufacturer;
        this.rating = rating;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Double getRating() {
        return rating;
    }
}
