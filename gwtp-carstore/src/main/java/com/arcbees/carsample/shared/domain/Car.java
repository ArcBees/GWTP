package com.arcbees.carsample.shared.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.GwtTransient;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Car extends BaseEntity {
    private String model;
    @GwtTransient
    private List<Rating> ratings;
    private Manufacturer manufacturer;
    private CarProperties carProperties;

    public Car() {
        this.model = "";
        this.carProperties = new CarProperties();
        this.ratings = new ArrayList<Rating>();
    }

    public Car(String model, Manufacturer manufacturer, CarProperties carProperties) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.carProperties = carProperties;
        this.ratings = new ArrayList<Rating>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public CarProperties getCarProperties() {
        return carProperties;
    }

    public void setCarProperties(CarProperties carProperties) {
        this.carProperties = carProperties;
    }
}
