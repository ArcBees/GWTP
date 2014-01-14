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

package com.gwtplatform.carstore.cucumber.application.cars;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.ByDebugId;

public class CarsPage extends PageWithEditTable {
    public void clickOnCreate() {
        getCreateButton().click();
    }

    public void fillForm() {
    }

    public void deleteFirstCar() {
        deleteFirstRow(getCarsTable());
    }

    public int getNumberOfLines() {
        return getNumberOfLines(getCarsTable());
    }

    public void editFirstCar() {
        editFirstRow(getCarsTable());
    }

    public String getFirstCarManufacturer() {
        return getCellText(getCarsTable(), "Manufacturer", 1);
    }

    public String getFirstCarModel() {
        return getCellText(getCarsTable(), "Model", 1);
    }

    private WebElement getCarsTable() {
        return waitUntilElementIsLoaded(ByDebugId.id("cars"));
    }

    private WebElement getCreateButton() {
        return waitUntilElementIsClickable(ByDebugId.id("carCreate"));
    }
}
