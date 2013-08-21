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

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CarPropertiesDto extends BaseEntity {
    private String someString;
    private Integer someNumber;
    private Date someDate;
    private CarDto car;

    public CarPropertiesDto() {
        this.someString = "";
        this.someNumber = 0;
        this.someDate = new Date();
    }

    public CarPropertiesDto(String someString,
                            Integer someNumber,
                            Date someDate) {
        this.someString = someString;
        this.someNumber = someNumber;
        this.someDate = someDate;
    }

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto carDto) {
        this.car = carDto;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public Integer getSomeNumber() {
        return someNumber;
    }

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }

    public Date getSomeDate() {
        return someDate;
    }

    public void setSomeDate(Date someDate) {
        this.someDate = someDate;
    }
}
