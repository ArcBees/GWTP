package com.arcbees.carsample.shared.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gwt.user.client.rpc.GwtTransient;

@Entity
@Table(name = "manufacturers")
public class Manufacturer implements BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
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

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
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
