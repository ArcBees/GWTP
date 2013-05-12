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

package com.gwtplatform.carstore.cucumber.application.manufacturers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class ManufacturersPage extends PageWithEditTable {
    @FindByDebugId("manufacturers")
    private WebElement manufacturers;
    @FindByDebugId("create-manufacturer")
    private WebElement create;

    public void clickOnCreate() {
        create.click();
    }

    public void deleteFirstManufacturer() {
        deleteFirstRow(manufacturers);
    }

    public int getNumberOfLines() {
        return getNumberOfLines(manufacturers);
    }

    public void editFirstManufacturer() {
        getFirstRowEdit().click();
    }

    public String getFirstManufacturer() {
        return getCellText(manufacturers, "Name", 1);
    }

    public String getLastManufacturer() {
        return getCellText(manufacturers, "Name", getNumberOfLines());
    }

    private WebElement getFirstRowEdit() {
        return waitUntilElementIsLoaded(manufacturers, By.cssSelector("tbody td:nth-last-child(2) button"));
    }
}
