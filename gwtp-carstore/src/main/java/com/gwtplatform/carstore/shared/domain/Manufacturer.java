package com.gwtplatform.carstore.shared.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Index
@Entity
public class Manufacturer extends BaseEntity {
    private String name;
    @Embed
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
