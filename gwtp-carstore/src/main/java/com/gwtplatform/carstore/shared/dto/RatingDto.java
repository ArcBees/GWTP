/*
 * Copyright 2013 ArcBees Inc.
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

public class RatingDto extends BaseEntity {
    private Integer rating;
    private CarDto car;

    public RatingDto() {
    }

    public RatingDto(CarDto car,
                     Integer rating) {
        this.car = car;
        this.rating = rating;
    }

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto car) {
        this.car = car;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        if (car != null && car.getManufacturer() != null) {
            ManufacturerDto manufacturerDto = car.getManufacturer();
            return manufacturerDto.getName() + "/" + car.getModel();
        }

        return super.toString();
    }
}
