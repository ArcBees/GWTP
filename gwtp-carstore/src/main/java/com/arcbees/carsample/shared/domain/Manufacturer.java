package com.arcbees.carsample.shared.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.google.gwt.user.client.rpc.GwtTransient;

@Entity
public class Manufacturer extends BaseEntity {
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manufacturer", cascade = CascadeType.REMOVE)
    @GwtTransient
    private List<Car> cars;

    public Manufacturer() {
        this.name = "";
        this.cars = new ArrayList<Car>();
    }

    public Manufacturer(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}
