package com.gwtplatform.carstore.server.dao.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.gwtplatform.carstore.server.dao.objectify.Deref;
import com.gwtplatform.carstore.shared.domain.BaseEntity;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

@Index
@Entity
public class Manufacturer extends BaseEntity {
    public static List<ManufacturerDto> createDto(List<Manufacturer> manufacturers) {
        if (manufacturers == null) {
            return null;
        }
        
        List<ManufacturerDto> manufacturerDto = new ArrayList<ManufacturerDto>();
        for (Manufacturer manufacturer : manufacturers) {
            manufacturerDto.add(createDto(manufacturer));
        }
        
        return manufacturerDto;
    }
    
    public static ManufacturerDto createDto(Manufacturer manufacturer) {
        if (manufacturer == null) {
            return null;
        }
        
        ManufacturerDto manufacturerDto = new ManufacturerDto();
        manufacturerDto.setCars(Car.createDto(manufacturer.getCars()));
        manufacturer.setId(manufacturer.getId());
        manufacturer.setName(manufacturer.getName());
        
        return manufacturerDto;
    }
    
    public static Manufacturer create(ManufacturerDto manufacturerDto) {
        if (manufacturerDto == null) { 
            return null;
        }
        
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCars(Car.create(manufacturerDto.getCars()));
        manufacturer.setId(manufacturerDto.getId());
        manufacturer.setName(manufacturerDto.getName());
        
        return manufacturer;
    }
    
    private String name;
    @Load
    private HashSet<Ref<Car>> cars;

    public Manufacturer() {
        this.name = "";
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
        if (this.cars == null) {
            return null;
        }
        
        List<Car> rcars = new ArrayList<Car>();
        for (Ref<Car> car : cars) {
            rcars.add(Deref.deref(car));
        }
        
        return rcars;
    }

    public void setCars(List<Car> cars) {
        if (cars == null) {
            this.cars = null;
        } else {
            for (Car car : cars) {
                if (this.cars == null) {
                    this.cars = new HashSet<Ref<Car>>();
                }
                this.cars.add(Ref.create(car));
            }
        }
    }
}
