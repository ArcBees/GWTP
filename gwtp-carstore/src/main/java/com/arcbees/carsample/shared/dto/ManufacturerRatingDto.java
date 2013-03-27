package com.arcbees.carsample.shared.dto;

public class ManufacturerRatingDto implements Dto {

    private static final long serialVersionUID = 5855002932629231837L;

    private String manufacturer;

    private Double rating;

    @SuppressWarnings("unused")
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
