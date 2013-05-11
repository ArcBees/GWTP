package com.gwtplatform.carstore.shared.dto;

import java.util.ArrayList;
import java.util.List;

public class ManufacturerDto extends BaseEntity {
    private String name;
    private List<CarDto> cars;

    public ManufacturerDto() {
        this.name = "";
        this.cars = new ArrayList<CarDto>();
    }

    public ManufacturerDto(String name) {
        this.name = name;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CarDto> getCars() {
        return cars;
    }

    public void setCars(List<CarDto> cars) {
        this.cars = cars;
    }
}
