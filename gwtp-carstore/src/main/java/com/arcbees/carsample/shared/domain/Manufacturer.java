package com.arcbees.carsample.shared.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.GwtTransient;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Manufacturer extends BaseEntity {
    private String name;
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
