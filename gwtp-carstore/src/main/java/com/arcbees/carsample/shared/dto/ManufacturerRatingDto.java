package com.arcbees.carsample.shared.dto;

public class ManufacturerRatingDto implements Dto {
    private String manufacturer;

    private Double rating;

    protected ManufacturerRatingDto() {
        // Needed for serialization
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
