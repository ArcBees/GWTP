package com.arcbees.carsample.shared.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.gwt.user.client.rpc.GwtTransient;

@Entity
@Table(name = "cars")
public class Car implements BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String model;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "car", cascade = CascadeType.REMOVE)
    @GwtTransient
    private List<Rating> ratings;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "car_properties_id", nullable = false)
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

    @Override
    public Integer getId() {
        return id;
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
