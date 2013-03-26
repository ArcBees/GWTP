package com.arcbees.carsample.shared.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.gwt.user.client.rpc.GwtTransient;

@Entity
public class Car extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Manufacturer manufacturer;

    private String model;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "car", cascade = CascadeType.REMOVE)
    @GwtTransient
    private List<Rating> ratings;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
