/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.shared.dto;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CarDto extends BaseEntity {
    private ManufacturerDto manufacturer;
    private String model;
    private List<RatingDto> ratings;
    private CarPropertiesDto carProperties;

    public CarDto() {
        this.model = "";
        this.carProperties = new CarPropertiesDto();
        this.ratings = new ArrayList<RatingDto>();
    }

    public CarDto(String model,
                  ManufacturerDto manufacturer,
                  CarPropertiesDto carProperties) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.carProperties = carProperties;
        this.ratings = new ArrayList<RatingDto>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerDto manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<RatingDto> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingDto> ratings) {
        this.ratings = ratings;
    }

    public CarPropertiesDto getCarProperties() {
        return carProperties;
    }

    public void setCarProperties(CarPropertiesDto carProperties) {
        this.carProperties = carProperties;
    }
}
