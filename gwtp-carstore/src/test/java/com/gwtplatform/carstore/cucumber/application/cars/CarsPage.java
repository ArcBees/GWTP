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

package com.gwtplatform.carstore.cucumber.application.cars;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class CarsPage extends PageWithEditTable {
    @FindByDebugId("cars")
    private WebElement cars;

    @FindByDebugId("carCreate")
    private WebElement create;

    public void clickOnCreate() {
        create.click();
    }

    public void fillForm() {
    }

    public void deleteFirstCar() {
        deleteFirstRow(cars);
    }

    public int getNumberOfLines() {
        return getNumberOfLines(cars);
    }

    public void editFirstCar() {
        editFirstRow(cars);
    }

    public String getFirstCarManufacturer() {
        return getCellText(cars, "Manufacturer", 1);
    }

    public String getFirstCarModel() {
        return getCellText(cars, "Model", 1);
    }
}
